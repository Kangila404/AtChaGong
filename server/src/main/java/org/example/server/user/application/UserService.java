package org.example.server.user.application;

import java.util.List;
import lombok.RequiredArgsConstructor;

import org.example.server.user.domain.models.ProfileImg;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.ProfileImgRepository;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.example.server.user.presentation.dto.req.OnboardingRequest;
import org.example.server.user.presentation.dto.req.UpdateNicknameRequest;

import org.example.server.user.presentation.dto.req.UpdateProfileImgRequest;
import org.example.server.user.presentation.dto.res.OnboardingResponse;
import org.example.server.user.presentation.dto.res.ProfileImgResponse;
import org.example.server.user.presentation.dto.res.UpdateNicknameResponse;
import org.example.server.user.presentation.dto.res.UpdateProfileImgResponse;
import org.example.server.user.presentation.dto.res.UserMeResponse;
import org.example.server.user.presentation.dto.res.UserProfileResponse;
import org.example.server.user.presentation.dto.res.WithdrawUserResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileImgRepository profileImgRepository;

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

    @Transactional(readOnly = true)
    public List<ProfileImgResponse> getProfileImgs(){
        return profileImgRepository.findAll().stream()
            .map(ProfileImgResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String userId){
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UpdateProfileImgResponse updateProfile(
        String userId,
        UpdateProfileImgRequest request
    ){
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);

        ProfileImg profileImg = findProfileImgOrThrow(request.profileId());

        user.updateProfile(profileImg);

        return UpdateProfileImgResponse.from(user);
    }

    // 온보딩 메서드
    @Transactional
    public OnboardingResponse onboarding(String userId, OnboardingRequest request){
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);

        if (!request.completed()) {
            throw new UserException(UserErrorCode.INCOMPLETE_ONBOARDING);
        }

        user.completeOnboarding();
        return new OnboardingResponse(true);
    }




    // 조회 메서드 모음
    // 1. (string) userId -> User 조회
    private User findUserByUserIdOrThrow(String userId){
        return userRepository.findByUserId(userId)
            .orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    // 2. profile_id -> ProfileImg 조회
    private ProfileImg findProfileImgOrThrow(Long profileId){
        return profileImgRepository.findById(profileId)
            .orElseThrow(() -> new UserException(UserErrorCode.PROFILE_NOT_FOUND));
    }

    // 검증 메서드 모음
    // 1. 유저 상태 검증
    private void validateUserStatus(User user){
        switch (user.getUserStatus()) {
            case WITHDRAWN -> throw new UserException(UserErrorCode.WITHDRAWN_USER);
            case SUSPENDED -> throw new UserException(UserErrorCode.SUSPENDED_USER);
            case ACTIVE -> { }
        }
    }
}
