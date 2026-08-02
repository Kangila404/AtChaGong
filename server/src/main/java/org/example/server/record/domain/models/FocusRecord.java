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
    @JoinColumn(name = "beverage_id", nullable = false)
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
}