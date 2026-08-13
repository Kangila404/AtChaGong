package org.example.server.user.domain.repository;

import java.util.List;
import java.util.Optional;
import org.example.server.user.domain.models.ProfileImg;

public interface ProfileImgRepository {

    Optional<ProfileImg> findById(Long id);
    ProfileImg save(ProfileImg profileImg);
    List<ProfileImg> findAll();
}
