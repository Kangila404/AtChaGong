package org.example.server.beverage.domain.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.common.entity.BaseEntity;
import org.example.server.user.domain.models.User;

@Getter
@Entity
@Table(
    name = "selected_beverage",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_selected_beverage_user", columnNames = "user_id"),
        @UniqueConstraint(
            name = "uk_selected_beverage_user_beverage",
            columnNames = "user_beverage_id"
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectedBeverage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_beverage_id", nullable = false)
    private UserBeverage userBeverage;

    @Builder(access = AccessLevel.PRIVATE)
    private SelectedBeverage(User user, UserBeverage userBeverage) {
        this.user = user;
        this.userBeverage = userBeverage;
    }

    public static SelectedBeverage create(User user, UserBeverage userBeverage) {
        return SelectedBeverage.builder()
            .user(user)
            .userBeverage(userBeverage)
            .build();
    }

    public void select(UserBeverage userBeverage) {
        this.userBeverage = userBeverage;
    }
}
