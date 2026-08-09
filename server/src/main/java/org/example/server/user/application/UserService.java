package org.example.server.user.application;

import lombok.RequiredArgsConstructor;

import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.presentation.dto.req.OnboardingRequest;
import org.example.server.user.presentation.dto.req.UpdateNicknameRequest;

import org.example.server.user.presentation.dto.res.OnboardingResponse;
import org.example.server.user.presentation.dto.res.UpdateNicknameResponse;
import org.example.server.user.presentation.dto.res.UserMeResponse;
import org.example.server.user.presentation.dto.res.WithdrawUserResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserMeResponse getMe(String userId){
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);
        return UserMeResponse.from(user);
    }

    @Transactional
    public UpdateNicknameResponse updateNickname(String userId, UpdateNicknameRequest request){
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);
        user.updateNickname(request.nickname());
        return UpdateNicknameResponse.from(user);
    }

    @Transactional
    public WithdrawUserResponse withdraw(String userId){
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);
        user.withdraw();
        return new WithdrawUserResponse("success");
    }

    @Transactional
    public OnboardingResponse onboarding(String userId, OnboardingRequest request){
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);

        if (!request.completed()) {
            throw new IllegalArgumentException("온보딩이 완료되지 않았습니다.");
        }

        user.completeOnboarding();
        return new OnboardingResponse(true);
    }




    // 조회 메서드 모음
    // 1. (string) userId -> User 조회
    private User findUserByUserIdOrThrow(String userId){
        return userRepository.findByUserId(userId)
            .orElseThrow(()-> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    // 검증 메서드 모음
    // 1. 유저 상태 검증
    private void validateUserStatus(User user){
        if(!user.getUserStatus().equals(UserStatus.ACTIVE)){
            throw new IllegalArgumentException("비활성 유저입니다.");
        }
    }
}
