package org.example.server.admin.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.admin.domain.models.Notice;
import org.example.server.admin.domain.repository.NoticeRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeJpaRepository noticeJpaRepository;

    @Override
    public Optional<Notice> findById(Long id) {
        return Optional.empty();
    }
}
