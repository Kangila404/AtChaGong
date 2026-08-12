package org.example.server.auth.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.common.entity.BaseEntity;

@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private long userId;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column
    private LocalDateTime revokedAt;

    // 비즈니스 로직
    // 1. 토큰 만료 확인
    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiredAt);
    }

    // 2. 재로그인 시 토큰 날짜 업데이트
    public void updateToken(String refreshToken, LocalDateTime expiredAt){
        this.tokenHash = refreshToken;
        this.expiredAt = expiredAt;
        this.revokedAt = null;
    }
    // 3. 리프레시 토큰 폐기 확인
    public boolean isRevoked() {return revokedAt != null;
    }
}
