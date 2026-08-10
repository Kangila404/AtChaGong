package org.example.server.notice.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.common.entity.BaseEntity;

@Getter
@Entity
@Table(name = "notice")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공지를 작성한 관리자의 User.id
    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private Notice(Long adminId, String title, String content) {
        this.adminId = adminId;
        this.title = title;
        this.content = content;
    }

    public static Notice create(Long adminId, String title, String content) {
        return Notice.builder()
            .adminId(adminId)
            .title(title)
            .content(content)
            .build();
    }

    public void update(String title, String content) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
    }
}
