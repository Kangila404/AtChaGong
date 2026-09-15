package org.example.server.beverage.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.beverage.domain.enums.BeverageAcquisitionType;
import org.example.server.common.entity.BaseEntity;
import org.example.server.user.domain.models.User;

@Getter
@Entity
@Table(
    name = "user_beverage",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_beverage_user_beverage",
            columnNames = {"user_id", "beverage_id"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBeverage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beverage_id", nullable = false)
    private Beverage beverage;

    @Enumerated(EnumType.STRING)
    @Column(name = "acquisition_type", nullable = false, length = 20)
    private BeverageAcquisitionType acquisitionType;

    @Column(name = "acquired_at", nullable = false)
    private LocalDateTime acquiredAt;

    @Builder(access = AccessLevel.PRIVATE)
    private UserBeverage(
        User user,
        Beverage beverage,
        BeverageAcquisitionType acquisitionType,
        LocalDateTime acquiredAt
    ) {
        this.user = user;
        this.beverage = beverage;
        this.acquisitionType = acquisitionType;
        this.acquiredAt = acquiredAt;
    }

    public static UserBeverage create(
        User user,
        Beverage beverage,
        BeverageAcquisitionType acquisitionType,
        LocalDateTime acquiredAt
    ) {
        return UserBeverage.builder()
            .user(user)
            .beverage(beverage)
            .acquisitionType(acquisitionType)
            .acquiredAt(acquiredAt)
            .build();
    }
}
