package org.example.server.notice.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.notice.domain.enums.NoticeStatus;
import org.example.server.notice.domain.models.Notice;
import org.example.server.notice.domain.repository.NoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeJpaRepository noticeJpaRepository;

    @Override
    public Page<Notice> findAll(Pageable pageable) {
        return noticeJpaRepository.findAll(pageable);
    }

    @Override
    public Page<Notice> findByStatus(NoticeStatus status, Pageable pageable) {
        return noticeJpaRepository.findByStatus(status, pageable);
    }

    @Override
    public Optional<Notice> findById(Long id) {
        return noticeJpaRepository.findById(id);
    }

    @Override
    public Optional<Notice> findByIdAndStatus(Long id, NoticeStatus status) {
        return noticeJpaRepository.findByIdAndStatus(id, status);
    }

    @Override
    public Notice save(Notice notice) {
        return noticeJpaRepository.save(notice);
    }

    @Override
    public void delete(Notice notice) {
        noticeJpaRepository.delete(notice);
    }
}
