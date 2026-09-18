package org.example.server.coin.domain.models;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.common.entity.BaseEntity;
import org.example.server.user.domain.models.User;

@Getter
@Entity
@Table(
    name = "coin_transaction",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_coin_transaction_source",
        columnNames = {"user_id", "transaction_type", "reference_type", "reference_id"}
    )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoinTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    private CoinTransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = false, length = 30)
    private CoinReferenceType referenceType;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "balance_after", nullable = false)
    private long balanceAfter;

    @Builder(access = AccessLevel.PRIVATE)
    private CoinTransaction(
        User user,
        long amount,
        CoinTransactionType transactionType,
        CoinReferenceType referenceType,
        Long referenceId,
        long balanceAfter
    ) {
        this.user = user;
        this.amount = amount;
        this.transactionType = transactionType;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.balanceAfter = balanceAfter;
    }

    public static CoinTransaction create(
        User user,
        long amount,
        CoinTransactionType transactionType,
        CoinReferenceType referenceType,
        Long referenceId,
        long balanceAfter
    ) {
        return CoinTransaction.builder()
            .user(user)
            .amount(amount)
            .transactionType(transactionType)
            .referenceType(referenceType)
            .referenceId(referenceId)
            .balanceAfter(balanceAfter)
            .build();
    }
}
