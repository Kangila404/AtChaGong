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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.common.entity.BaseEntity;


@Table(name = "timer_setting")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimerSetting  extends BaseEntity {

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
}
