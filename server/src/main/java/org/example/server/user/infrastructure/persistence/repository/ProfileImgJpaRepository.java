package org.example.server.user.infrastructure.persistence.repository;

import org.example.server.user.domain.models.ProfileImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImgJpaRepository extends JpaRepository<ProfileImg,Long> {

}
