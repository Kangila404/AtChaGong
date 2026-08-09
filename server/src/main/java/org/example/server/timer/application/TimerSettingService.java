package org.example.server.timer.application;

import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.timer.domain.models.TimerSetting;
import org.example.server.timer.domain.repository.TimerSettingRepository;
import org.example.server.timer.presentation.dto.req.SaveTimerRequest;
import org.example.server.timer.presentation.dto.res.SaveTimerResponse;
import org.example.server.timer.presentation.dto.res.TimerSettingResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimerSettingService {

    private final UserRepository userRepository;
    private final TimerSettingRepository timerSettingRepository;
    private final BeverageRepository beverageRepository;

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
        Beverage beverage = findBeverageByIdOrElseThrow(request.beverageId());

        TimerSetting timerSetting = timerSettingRepository.findByUserId(user.getId()).orElse(null);

        if (timerSetting == null) {
            timerSetting = TimerSetting.create(
                user.getId(), beverage,
                request.focusMinutes(), request.breakMinutes(), request.cycleCount()
            );
            timerSettingRepository.save(timerSetting);
        } else {
            timerSetting.update(beverage, request.focusMinutes(), request.breakMinutes(), request.cycleCount());
        }

        return SaveTimerResponse.from(timerSetting);
    }

    // ================ 조회 메서드 모음 ================ //
    private User findUserByUserIdOrElseThrow(String userId){
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    private Beverage findBeverageByIdOrElseThrow(Long beverageId){
        if (beverageId == null) {
            throw new IllegalArgumentException("beverageId는 필수입니다.");
        }
        return beverageRepository.findById(beverageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 음료입니다."));
    }

    // ================ 검증 메서드 모음 ================ //
    private void validateUserStatus(UserStatus userStatus){
        if(!userStatus.equals(UserStatus.ACTIVE)){
            throw new IllegalArgumentException("비활성화된 유저입니다.");
        }
    }

    private void validateTimerValues(SaveTimerRequest request){
        if(request.focusMinutes() == null || request.focusMinutes() <= 0){
            throw new IllegalArgumentException("집중 시간은 1분 이상이어야 합니다.");
        }
        if(request.breakMinutes() == null || request.breakMinutes() <= 0){
            throw new IllegalArgumentException("휴식 시간은 1분 이상이어야 합니다.");
        }
        if(request.cycleCount() == null || request.cycleCount() <= 0){
            throw new IllegalArgumentException("반복 횟수는 1회 이상이어야 합니다.");
        }
    }
}