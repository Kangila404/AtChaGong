package org.example.server.notice.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.common.entity.BaseEntity;
import org.example.server.notice.domain.enums.NoticeStatus;

@Getter
@Entity
@Table(name = "notice")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    public static final int MAX_TITLE_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공지를 작성한 관리자의 User.id
    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(nullable = false, length = MAX_TITLE_LENGTH)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "img_url", length = 512)
    private String imgUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NoticeStatus status;

    @Column(name = "publish_starts_at", nullable = false)
    private LocalDateTime publishStartsAt;

    @Column(name = "publish_ends_at")
    private LocalDateTime publishEndsAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Notice(
        Long adminId,
        String title,
        String content,
        String imgUrl,
        NoticeStatus status,
        LocalDateTime publishStartsAt,
        LocalDateTime publishEndsAt
    ) {
        this.adminId = adminId;
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.status = status;
        this.publishStartsAt = publishStartsAt;
        this.publishEndsAt = publishEndsAt;
    }

    public static Notice create(
        Long adminId,
        String title,
        String content,
        String imgUrl,
        NoticeStatus status,
        LocalDateTime publishStartsAt,
        LocalDateTime publishEndsAt
    ) {
        return Notice.builder()
            .adminId(adminId)
            .title(title)
            .content(content)
            .imgUrl(imgUrl)
            .status(status)
            .publishStartsAt(publishStartsAt)
            .publishEndsAt(publishEndsAt)
            .build();
    }

    public void update(
        String title,
        String content,
        String imgUrl,
        NoticeStatus status,
        LocalDateTime publishStartsAt,
        LocalDateTime publishEndsAt
    ) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (imgUrl != null) {
            this.imgUrl = imgUrl;
        }
        if (status != null) {
            this.status = status;
        }
        if (publishStartsAt != null) {
            this.publishStartsAt = publishStartsAt;
        }
        if (publishEndsAt != null) {
            this.publishEndsAt = publishEndsAt;
        }
    }
}
