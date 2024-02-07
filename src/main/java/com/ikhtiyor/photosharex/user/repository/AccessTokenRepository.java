package com.ikhtiyor.photosharex.user.repository;

import com.ikhtiyor.photosharex.user.model.AccessToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    Optional<AccessToken> findByToken(String token);
}
