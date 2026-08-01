package org.example.server.auth.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.auth.domain.enums.AuthType;
import org.example.server.common.entity.BaseEntity;
import org.example.server.user.domain.models.User;


@Getter
@Entity
@Table(
    name = "auth_account",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_auth_provider", columnNames = {"provider", "provider_id"})
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private AuthType provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

}
