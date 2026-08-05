package org.example.server.user.application;

import lombok.RequiredArgsConstructor;
import org.example.server.record.domain.repository.FocusRecordRepository;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.presentation.dto.req.UserMeRequest;
import org.example.server.user.presentation.dto.res.UserMeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserMeResponse getMe(String userId){
        User user = findUserByUserIdOrThrow(userId);
        validateUseStatus(user);
        return UserMeResponse.from(user);
    }




    // 조회 메서드 모음
    // 1. (string) userId -> User 조회
    private User findUserByUserIdOrThrow(String userId){
        return userRepository.findByUserId(userId)
            .orElseThrow(()-> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    // 검증 메서드 모음
    // 1. 유저 상태 검증
    private void validateUseStatus(User user){
        if(!user.getUserStatus().equals(UserStatus.ACTIVE)){
            throw new IllegalArgumentException("비활성 유저입니다.");
        }
    }
}
