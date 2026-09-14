package org.example.server.beverage.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.beverage.domain.enums.BeverageSaleStatus;
import org.example.server.common.entity.BaseEntity;

@Getter
@Entity
@Table(name = "beverage")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Beverage extends BaseEntity {

    public static final int MAX_NAME_LENGTH = 255;
    public static final int MAX_IMG_URL_LENGTH = 512;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(name = "img_url", nullable = false, length = MAX_IMG_URL_LENGTH)
    private String imgUrl;

    @Column(nullable = false)
    private Long price;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", nullable = false, length = 20)
    private BeverageSaleStatus saleStatus;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "sale_ends_at")
    private LocalDateTime saleEndsAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Beverage(
        String name,
        String imgUrl,
        Long price,
        Integer displayOrder,
        BeverageSaleStatus saleStatus,
        boolean isDefault,
        LocalDateTime saleEndsAt
    ) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.price = price;
        this.displayOrder = displayOrder;
        this.saleStatus = saleStatus;
        this.isDefault = isDefault;
        this.saleEndsAt = saleEndsAt;
    }

    public static Beverage create(
        String name,
        String imgUrl,
        Long price,
        Integer displayOrder,
        BeverageSaleStatus saleStatus,
        boolean isDefault,
        LocalDateTime saleEndsAt
    ) {
        return Beverage.builder()
            .name(name)
            .imgUrl(imgUrl)
            .price(price)
            .displayOrder(displayOrder)
            .saleStatus(saleStatus)
            .isDefault(isDefault)
            .saleEndsAt(saleEndsAt)
            .build();
    }

    public boolean isLimited() {
        return saleEndsAt != null;
    }
}
