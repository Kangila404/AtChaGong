package org.example.server.user.domain.models;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.common.entity.BaseEntity;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;

@Builder
@Getter
@Entity
@Table(name = "users")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    // 닉네임 설정용(닉네임 필수 아님...)
    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private ProfileImg profileImg;

    // ============ 비즈니스 로직 ============ //
    // 1. 유저 생성
    public static User createSocialUser(ProfileImg defaultProfileImg) {
        String userId = UUID.randomUUID().toString();

        return User.builder()
            .userId(userId)
            .nickname(createTemporaryNickname(userId))
            .userStatus(UserStatus.ACTIVE)
            .userRole(UserRole.USER)
            .onboardingCompleted(false)
            .profileImg(defaultProfileImg)
            .build();
    }

    // 2. 마지막 로그인 업데이트
    public void updateLastLoginAt(){
        this.lastLoginAt = LocalDateTime.now(KST);
    }

    // 3. 닉네임 업데이트
    public void updateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }
        this.nickname = nickname;
    }

    // 4. 회원탈퇴
    public void withdraw(){
        if(this.userStatus == UserStatus.WITHDRAWN){
            throw new UserException(UserErrorCode.ALREADY_WITHDRAWN_USER);
        }
        this.userStatus = UserStatus.WITHDRAWN;
        this.deletedAt = LocalDateTime.now(KST);
    }

    // 4-1. 탈퇴 회원 재가입 처리
    public void reactivateForRejoin(ProfileImg defaultProfileImg) {
        if (this.userStatus != UserStatus.WITHDRAWN) {
            return;
        }

        this.nickname = createTemporaryNickname(this.userId);
        this.userStatus = UserStatus.ACTIVE;
        this.onboardingCompleted = false;
        this.deletedAt = null;
        this.profileImg = defaultProfileImg;
    }

    // 5. 온보딩 처리
    public void  completeOnboarding(){
        this.onboardingCompleted = true;
    }

    // 6. 프로필 이미지 업데이트
    public void updateProfile(ProfileImg profileImg){
        this.profileImg = profileImg;
    }

    private static String createTemporaryNickname(String userId) {
        String normalizedUserId = userId.replace("-", "");
        return "사용자" + normalizedUserId.substring(0, Math.min(6, normalizedUserId.length()));
    }
}
