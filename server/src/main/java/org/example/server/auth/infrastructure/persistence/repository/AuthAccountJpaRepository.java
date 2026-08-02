package org.example.server.auth.infrastructure.persistence.repository;

import org.example.server.auth.domain.models.AuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthAccountJpaRepository extends JpaRepository<AuthAccount,Long> {

}
