package com.ikhtiyor.photosharex.photo.repository;

import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.user.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Page<Photo> findByUser(User user, Pageable pageable);
    List<Photo> findByUserAndIdIn(User user, List<Long> photoIds);

    Optional<Photo> findByUserAndId(User user, Long photoId);
}
