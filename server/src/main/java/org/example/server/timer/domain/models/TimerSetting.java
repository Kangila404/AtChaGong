package org.example.server.timer.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Timer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.common.entity.BaseEntity;


@Builder
@Table(name = "timer_setting")
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimerSetting  extends BaseEntity {

    // 디폴트 값
    public static final int DEFAULT_FOCUS_MINUTES = 25;
    public static final int DEFAULT_BREAK_MINUTES = 5;
    public static final int DEFAULT_CYCLE_COUNT = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beverage_id", nullable = false)
    private Beverage beverage;

    @Column(name = "focus_minutes", nullable = false)
    private int focusMinutes;

    @Column(name = "break_minutes", nullable = false)
    private int breakMinutes;

    @Column(name = "cycle_count", nullable = false)
    private int cycleCount;

    // 비즈니스 로직
    // 1. 생성
    public static TimerSetting create(Long userId, Beverage beverage, int focusMinutes, int breakMinutes, int cycleCount) {
        return TimerSetting.builder()
            .userId(userId)
            .beverage(beverage)
            .focusMinutes(focusMinutes)
            .breakMinutes(breakMinutes)
            .cycleCount(cycleCount)
            .build();
    }

    // 2. 수정
    public void update(Beverage beverage, int focusMinutes, int breakMinutes, int cycleCount) {
        this.beverage = beverage;
        this.focusMinutes = focusMinutes;
        this.breakMinutes = breakMinutes;
        this.cycleCount = cycleCount;
    }
}
