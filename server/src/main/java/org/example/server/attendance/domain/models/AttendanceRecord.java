package org.example.server.attendance.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.common.entity.BaseEntity;
import org.example.server.user.domain.models.User;

@Getter
@Entity
@Table(
    name = "attendance_record",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_attendance_user_date",
        columnNames = {"user_id", "attendance_date"}
    )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttendanceRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "consecutive_day", nullable = false)
    private int consecutiveDay;

    @Column(name = "granted_coin", nullable = false)
    private long grantedCoin;

    @Builder(access = AccessLevel.PRIVATE)
    private AttendanceRecord(User user, LocalDate attendanceDate, int consecutiveDay, long grantedCoin) {
        this.user = user;
        this.attendanceDate = attendanceDate;
        this.consecutiveDay = consecutiveDay;
        this.grantedCoin = grantedCoin;
    }

    public static AttendanceRecord create(User user, LocalDate attendanceDate, int consecutiveDay, long grantedCoin) {
        return AttendanceRecord.builder()
            .user(user)
            .attendanceDate(attendanceDate)
            .consecutiveDay(consecutiveDay)
            .grantedCoin(grantedCoin)
            .build();
    }
}
