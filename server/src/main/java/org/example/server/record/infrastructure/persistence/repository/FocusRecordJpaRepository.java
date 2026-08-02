package org.example.server.record.infrastructure.persistence.repository;

import org.example.server.record.domain.models.FocusRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FocusRecordJpaRepository extends JpaRepository<FocusRecord,Long> {

}
