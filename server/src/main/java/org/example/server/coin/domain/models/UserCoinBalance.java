package org.example.server.coin.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.common.entity.BaseEntity;
import org.example.server.user.domain.models.User;

@Getter
@Entity
@Table(name = "user_coin_balance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoinBalance extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private long balance;

    @Builder(access = AccessLevel.PRIVATE)
    private UserCoinBalance(User user, long balance) {
        this.user = user;
        this.balance = balance;
    }

    public static UserCoinBalance create(User user) {
        return UserCoinBalance.builder()
            .user(user)
            .balance(0)
            .build();
    }

    public void change(long amount) {
        this.balance += amount;
    }
}
