package org.example.server.record.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.common.entity.BaseEntity;

@Getter
@Entity
@Table(name = "focus_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FocusRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beverage_id")
    private Beverage beverage;

    // 설정된 집중 시간
    @Column(name = "focus_minutes", nullable = false)
    private int focusMinutes;

    // 실제 집중한 시간
    @Column(name = "focused_seconds", nullable = false)
    private int focusedSeconds;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    // 일별·월별 기록 조회용
    @Column(name = "focused_date", nullable = false)
    private LocalDate focusedDate;

    @Builder(access = AccessLevel.PRIVATE)
    private FocusRecord(
        Long userId,
        Beverage beverage,
        int focusMinutes,
        int focusedSeconds,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        LocalDate focusedDate
    ) {
        this.userId = userId;
        this.beverage = beverage;
        this.focusMinutes = focusMinutes;
        this.focusedSeconds = focusedSeconds;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.focusedDate = focusedDate;
    }

    public static FocusRecord create(
        Long userId,
        Beverage beverage,
        int focusMinutes,
        int focusedSeconds,
        LocalDateTime startedAt,
        LocalDateTime completedAt
    ) {
        return FocusRecord.builder()
            .userId(userId)
            .beverage(beverage)
            .focusMinutes(focusMinutes)
            .focusedSeconds(focusedSeconds)
            .startedAt(startedAt)
            .completedAt(completedAt)
            .focusedDate(completedAt.toLocalDate())
            .build();
    }
}
