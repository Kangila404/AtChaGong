package org.example.server.user.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.user.domain.models.ProfileImg;
import org.example.server.user.domain.repository.ProfileImgRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileImgRepositoryImpl implements ProfileImgRepository {

    private final ProfileImgJpaRepository profileImgJpaRepository;

    @Override
    public Optional<ProfileImg> findById(Long id) {
        return profileImgJpaRepository.findById(id);
    }

    @Override
    public ProfileImg save(ProfileImg profileImg) {
        return profileImgJpaRepository.save(profileImg);
    }

    @Override
    public List<ProfileImg> findAll() {
        return profileImgJpaRepository.findAll();
    }
}
