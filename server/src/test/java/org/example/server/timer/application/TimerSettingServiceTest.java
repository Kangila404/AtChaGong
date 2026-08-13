package org.example.server.timer.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.timer.domain.models.TimerSetting;
import org.example.server.timer.domain.repository.TimerSettingRepository;
import org.example.server.timer.exception.TimerErrorCode;
import org.example.server.timer.exception.TimerException;
import org.example.server.timer.presentation.dto.req.SaveTimerRequest;
import org.example.server.timer.presentation.dto.res.SaveTimerResponse;
import org.example.server.timer.presentation.dto.res.TimerSettingResponse;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimerSettingServiceTest {

    private static final String USER_ID = "user-1";
    private static final long USER_PK = 1L;
    private static final long BEVERAGE_ID = 10L;

    @InjectMocks
    private TimerSettingService timerSettingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TimerSettingRepository timerSettingRepository;

    @Mock
    private BeverageRepository beverageRepository;

    @Test
    @DisplayName("저장된 타이머 설정이 없으면 기본 설정을 반환한다")
    void getSettingWithoutSavedSettingReturnsDefault() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));
        given(timerSettingRepository.findByUserId(USER_PK)).willReturn(Optional.empty());

        TimerSettingResponse response = timerSettingService.getSetting(USER_ID);

        assertThat(response.focusMinutes()).isEqualTo(TimerSetting.DEFAULT_FOCUS_MINUTES);
        assertThat(response.breakMinutes()).isEqualTo(TimerSetting.DEFAULT_BREAK_MINUTES);
        assertThat(response.cycleCount()).isEqualTo(TimerSetting.DEFAULT_CYCLE_COUNT);
        assertThat(response.isCustomized()).isFalse();
        assertThat(response.beverage()).isNull();
    }

    @Test
    @DisplayName("타이머 설정이 없으면 새 설정을 저장한다")
    void saveSettingCreatesNewSetting() {
        Beverage beverage = beverage();
        SaveTimerRequest request = new SaveTimerRequest(BEVERAGE_ID, 30, 10, 3);
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));
        given(beverageRepository.findById(BEVERAGE_ID)).willReturn(Optional.of(beverage));
        given(timerSettingRepository.findByUserId(USER_PK)).willReturn(Optional.empty());
        given(timerSettingRepository.save(any(TimerSetting.class))).willAnswer(invocation -> invocation.getArgument(0));

        SaveTimerResponse response = timerSettingService.saveSetting(USER_ID, request);

        ArgumentCaptor<TimerSetting> captor = ArgumentCaptor.forClass(TimerSetting.class);
        verify(timerSettingRepository).save(captor.capture());
        TimerSetting saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(USER_PK);
        assertThat(saved.getBeverage()).isSameAs(beverage);
        assertThat(saved.getFocusMinutes()).isEqualTo(30);
        assertThat(saved.getBreakMinutes()).isEqualTo(10);
        assertThat(saved.getCycleCount()).isEqualTo(3);
        assertThat(response.focusMinutes()).isEqualTo(30);
    }

    @Test
    @DisplayName("집중 시간이 0 이하이면 타이머 설정을 저장하지 않는다")
    void saveSettingWithInvalidFocusMinutesThrowsException() {
        SaveTimerRequest request = new SaveTimerRequest(BEVERAGE_ID, 0, 10, 3);
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));

        assertThatThrownBy(() -> timerSettingService.saveSetting(USER_ID, request))
            .isInstanceOf(TimerException.class)
            .extracting("code")
            .isEqualTo(TimerErrorCode.INVALID_FOCUS_MINUTES.name());
        verify(beverageRepository, never()).findById(any());
        verify(timerSettingRepository, never()).save(any());
    }

    private User activeUser() {
        return User.builder()
            .id(USER_PK)
            .userId(USER_ID)
            .nickname("tester")
            .userStatus(UserStatus.ACTIVE)
            .userRole(UserRole.USER)
            .onboardingCompleted(true)
            .build();
    }

    private Beverage beverage() {
        Beverage beverage = org.mockito.Mockito.mock(Beverage.class);
        given(beverage.getId()).willReturn(BEVERAGE_ID);
        given(beverage.getName()).willReturn("americano");
        given(beverage.getImgUrl()).willReturn("https://example.com/americano.png");
        return beverage;
    }
}
