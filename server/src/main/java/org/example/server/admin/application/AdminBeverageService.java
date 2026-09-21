package org.example.server.admin.application;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.server.admin.presentation.dto.req.AdminBeverageCreateRequest;
import org.example.server.admin.presentation.dto.req.AdminBeverageOrderRequest;
import org.example.server.admin.presentation.dto.req.AdminBeveragePageRequest;
import org.example.server.admin.presentation.dto.req.AdminBeverageUpdateRequest;
import org.example.server.admin.presentation.dto.res.AdminBeveragePageResponse;
import org.example.server.admin.presentation.dto.res.AdminBeverageResponse;
import org.example.server.beverage.domain.enums.BeverageSaleStatus;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.exception.BeverageErrorCode;
import org.example.server.beverage.exception.BeverageException;
import org.example.server.common.exception.AtchagongException;
import org.example.server.common.exception.CommonErrorCode;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBeverageService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final BeverageRepository beverageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminBeveragePageResponse getBeverages(String userId, AdminBeveragePageRequest request) {
        findAdminOrThrow(userId);
        PageValues pageValues = parsePageRequest(request);
        PageRequest pageRequest = PageRequest.of(
            pageValues.page(),
            pageValues.size(),
            Sort.by(Sort.Order.asc("displayOrder"), Sort.Order.asc("id"))
        );
        Page<Beverage> beveragePage = pageValues.saleStatus() == null
            ? beverageRepository.findAll(pageRequest)
            : beverageRepository.findBySaleStatus(pageValues.saleStatus(), pageRequest);
        List<AdminBeverageResponse> content = beveragePage.getContent().stream()
            .map(this::toResponse)
            .toList();
        return AdminBeveragePageResponse.of(beveragePage, content);
    }

    @Transactional(readOnly = true)
    public AdminBeverageResponse getBeverage(String userId, String beverageId) {
        findAdminOrThrow(userId);
        return toResponse(findBeverageByIdOrThrow(parseBeverageId(beverageId)));
    }

    @Transactional
    public AdminBeverageResponse createBeverage(String userId, AdminBeverageCreateRequest request) {
        findAdminOrThrow(userId);
        CreateFields fields = validateCreateRequest(request);
        Beverage beverage = Beverage.create(
            fields.name(),
            fields.imgUrl(),
            fields.price(),
            fields.displayOrder(),
            BeverageSaleStatus.DRAFT,
            false,
            fields.saleEndsAt()
        );
        return toResponse(beverageRepository.save(beverage));
    }

    @Transactional
    public AdminBeverageResponse updateBeverage(String userId, String beverageId, AdminBeverageUpdateRequest request) {
        findAdminOrThrow(userId);
        Long parsedBeverageId = parseBeverageId(beverageId);
        Beverage beverage = findBeverageByIdOrThrow(parsedBeverageId);
        UpdateFields fields = validateUpdateRequest(beverage, request);
        beverage.update(
            fields.name(),
            fields.imgUrl(),
            fields.price(),
            fields.saleStatus(),
            fields.saleEndsAt(),
            fields.displayOrder()
        );
        return toResponse(beverage);
    }

    @Transactional
    public void updateDisplayOrders(String userId, AdminBeverageOrderRequest request) {
        findAdminOrThrow(userId);
        List<AdminBeverageOrderRequest.OrderItem> orders = validateOrderRequest(request);
        List<BeverageOrder> beverages = orders.stream()
            .map(order -> new BeverageOrder(
                findBeverageByIdOrThrow(order.beverageId()),
                order.displayOrder()
            ))
            .toList();
        beverages.forEach(order -> order.beverage().updateDisplayOrder(order.displayOrder()));
    }

    private User findAdminOrThrow(String userId) {
        User admin = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        if (admin.getUserStatus() != UserStatus.ACTIVE || admin.getUserRole() != UserRole.ADMIN) {
            throw new AtchagongException(CommonErrorCode.FORBIDDEN);
        }
        return admin;
    }

    private Beverage findBeverageByIdOrThrow(Long beverageId) {
        return beverageRepository.findById(beverageId)
            .orElseThrow(() -> new BeverageException(BeverageErrorCode.BEVERAGE_NOT_FOUND));
    }

    private Long parseBeverageId(String beverageId) {
        try {
            long parsedBeverageId = Long.parseLong(beverageId);
            if (parsedBeverageId < 1) {
                throw new NumberFormatException();
            }
            return parsedBeverageId;
        } catch (NumberFormatException exception) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_ID);
        }
    }

    private PageValues parsePageRequest(AdminBeveragePageRequest request) {
        if (request == null) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_PAGE_REQUEST);
        }
        int page = parsePageValue(request.page(), AdminBeveragePageRequest.DEFAULT_PAGE);
        int size = parsePageValue(request.size(), AdminBeveragePageRequest.DEFAULT_SIZE);
        if (page < 0 || size < 1 || size > AdminBeveragePageRequest.MAX_SIZE) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_PAGE_REQUEST);
        }
        BeverageSaleStatus saleStatus = "all".equalsIgnoreCase(request.status())
            ? null
            : parseSaleStatus(request.status());
        return new PageValues(page, size, saleStatus);
    }

    private int parsePageValue(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_PAGE_REQUEST);
        }
    }

    private CreateFields validateCreateRequest(AdminBeverageCreateRequest request) {
        if (request == null) {
            throw new AtchagongException(CommonErrorCode.INVALID_REQUEST);
        }
        return new CreateFields(
            validateName(request.name()),
            validateImgUrl(request.imgUrl()),
            validatePrice(request.price()),
            validateDisplayOrder(request.displayOrder() == null ? 0 : request.displayOrder()),
            validateFutureSaleEndsAt(request.saleEndsAt())
        );
    }

    private UpdateFields validateUpdateRequest(Beverage beverage, AdminBeverageUpdateRequest request) {
        if (request == null || !hasUpdatableField(request)) {
            throw new AtchagongException(CommonErrorCode.INVALID_REQUEST);
        }
        validateDefaultBeveragePolicy(beverage, request);
        return new UpdateFields(
            request.name() == null ? beverage.getName() : validateName(request.name()),
            request.imgUrl() == null ? beverage.getImgUrl() : validateImgUrl(request.imgUrl()),
            request.price() == null ? beverage.getPrice() : validatePrice(request.price()),
            request.saleStatus() == null ? beverage.getSaleStatus() : parseSaleStatus(request.saleStatus()),
            request.saleEndsAt() == null ? beverage.getSaleEndsAt() : validateFutureSaleEndsAt(request.saleEndsAt()),
            request.displayOrder() == null ? beverage.getDisplayOrder() : validateDisplayOrder(request.displayOrder())
        );
    }

    private boolean hasUpdatableField(AdminBeverageUpdateRequest request) {
        return request.name() != null || request.imgUrl() != null || request.price() != null
            || request.saleStatus() != null || request.saleEndsAt() != null || request.displayOrder() != null;
    }

    private void validateDefaultBeveragePolicy(Beverage beverage, AdminBeverageUpdateRequest request) {
        if (!beverage.isDefault()) {
            return;
        }
        boolean changesPrice = request.price() != null;
        boolean setsSaleEndsAt = request.saleEndsAt() != null;
        boolean endsSale = BeverageSaleStatus.ENDED.name().equalsIgnoreCase(request.saleStatus());
        if (changesPrice || setsSaleEndsAt || endsSale) {
            throw new BeverageException(BeverageErrorCode.DEFAULT_BEVERAGE_MODIFICATION_NOT_ALLOWED);
        }
    }

    private String validateName(String name) {
        if (name == null || name.isBlank() || name.length() > Beverage.MAX_NAME_LENGTH) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_NAME);
        }
        return name;
    }

    private Long validatePrice(Long price) {
        if (price == null || price < Beverage.MIN_SALE_PRICE) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_PRICE);
        }
        return price;
    }

    private String validateImgUrl(String imgUrl) {
        if (imgUrl == null || imgUrl.length() > Beverage.MAX_IMG_URL_LENGTH) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_IMAGE_URL);
        }
        try {
            URI uri = new URI(imgUrl);
            if (!uri.isAbsolute() || !"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
                throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_IMAGE_URL);
            }
            return imgUrl;
        } catch (URISyntaxException exception) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_IMAGE_URL);
        }
    }

    private BeverageSaleStatus parseSaleStatus(String saleStatus) {
        try {
            return BeverageSaleStatus.valueOf(saleStatus.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_SALE_STATUS);
        }
    }

    private LocalDateTime validateFutureSaleEndsAt(OffsetDateTime saleEndsAt) {
        if (saleEndsAt == null) {
            return null;
        }
        LocalDateTime convertedSaleEndsAt = saleEndsAt.atZoneSameInstant(SEOUL_ZONE).toLocalDateTime();
        if (!convertedSaleEndsAt.isAfter(LocalDateTime.now(SEOUL_ZONE))) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_SALE_ENDS_AT);
        }
        return convertedSaleEndsAt;
    }

    private Integer validateDisplayOrder(Integer displayOrder) {
        if (displayOrder == null || displayOrder < 0) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_DISPLAY_ORDER);
        }
        return displayOrder;
    }

    private List<AdminBeverageOrderRequest.OrderItem> validateOrderRequest(AdminBeverageOrderRequest request) {
        if (request == null || request.orders() == null || request.orders().isEmpty()) {
            throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_ORDER_REQUEST);
        }
        Set<Long> beverageIds = new HashSet<>();
        for (AdminBeverageOrderRequest.OrderItem order : request.orders()) {
            if (order == null || order.beverageId() == null || order.beverageId() < 1
                || !beverageIds.add(order.beverageId())) {
                throw new BeverageException(BeverageErrorCode.INVALID_BEVERAGE_ORDER_REQUEST);
            }
            validateDisplayOrder(order.displayOrder());
        }
        return request.orders();
    }

    private AdminBeverageResponse toResponse(Beverage beverage) {
        OffsetDateTime saleEndsAt = beverage.getSaleEndsAt() == null
            ? null
            : beverage.getSaleEndsAt().atZone(SEOUL_ZONE).toOffsetDateTime();
        return AdminBeverageResponse.of(beverage, saleEndsAt);
    }

    private record PageValues(int page, int size, BeverageSaleStatus saleStatus) {
    }

    private record CreateFields(
        String name,
        String imgUrl,
        Long price,
        Integer displayOrder,
        LocalDateTime saleEndsAt
    ) {
    }

    private record UpdateFields(
        String name,
        String imgUrl,
        Long price,
        BeverageSaleStatus saleStatus,
        LocalDateTime saleEndsAt,
        Integer displayOrder
    ) {
    }

    private record BeverageOrder(Beverage beverage, Integer displayOrder) {
    }
}
