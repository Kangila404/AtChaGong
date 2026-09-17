package org.example.server.timer.application;

import lombok.RequiredArgsConstructor;
import org.example.server.timer.domain.models.TimerSetting;
import org.example.server.timer.domain.repository.TimerSettingRepository;
import org.example.server.timer.exception.TimerErrorCode;
import org.example.server.timer.exception.TimerException;
import org.example.server.timer.presentation.dto.req.SaveTimerRequest;
import org.example.server.timer.presentation.dto.res.SaveTimerResponse;
import org.example.server.timer.presentation.dto.res.TimerSettingResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimerSettingService {

    private final UserRepository userRepository;
    private final TimerSettingRepository timerSettingRepository;

    @Transactional(readOnly = true)
    public TimerSettingResponse getSetting(String userId){
        User user = findUserByUserIdOrElseThrow(userId);
        validateUserStatus(user.getUserStatus());

        return timerSettingRepository.findByUserId(user.getId())
            .map(TimerSettingResponse::from)
            .orElseGet(TimerSettingResponse::defaultResponse);
    }

    @Transactional
    public SaveTimerResponse saveSetting(String userId, SaveTimerRequest request){
        User user = findUserByUserIdOrElseThrow(userId);
        validateUserStatus(user.getUserStatus());
        validateTimerValues(request);
        TimerSetting timerSetting = timerSettingRepository.findByUserId(user.getId()).orElse(null);

        if (timerSetting == null) {
            timerSetting = TimerSetting.create(
                user.getId(),
                request.focusMinutes(), request.breakMinutes(), request.cycleCount()
            );
            timerSettingRepository.save(timerSetting);
        } else {
            timerSetting.update(request.focusMinutes(), request.breakMinutes(), request.cycleCount());
        }

        return SaveTimerResponse.from(timerSetting);
    }

    // ================ 조회 메서드 모음 ================ //
    private User findUserByUserIdOrElseThrow(String userId){
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    // ================ 검증 메서드 모음 ================ //
    private void validateUserStatus(UserStatus userStatus){
        switch (userStatus) {
            case WITHDRAWN -> throw new UserException(UserErrorCode.WITHDRAWN_USER);
            case SUSPENDED -> throw new UserException(UserErrorCode.SUSPENDED_USER);
            case ACTIVE -> { }
        }
    }

    private void validateTimerValues(SaveTimerRequest request){
        if(request.focusMinutes() == null || request.focusMinutes() <= 0){
            throw new TimerException(TimerErrorCode.INVALID_FOCUS_MINUTES);
        }
        if(request.breakMinutes() == null || request.breakMinutes() <= 0){
            throw new TimerException(TimerErrorCode.INVALID_BREAK_MINUTES);
        }
        if(request.cycleCount() == null || request.cycleCount() <= 0){
            throw new TimerException(TimerErrorCode.INVALID_CYCLE_COUNT);
        }
    }
}
