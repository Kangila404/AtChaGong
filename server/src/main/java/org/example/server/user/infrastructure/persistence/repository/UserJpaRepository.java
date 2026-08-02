package org.example.server.user.infrastructure.persistence.repository;

import org.example.server.user.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

}
