package com.ikhtiyor.photosharex.photo.repository;

import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.user.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByUserAndId(User user, Long albumId);

    Page<Album> findByUser(User user, Pageable pageable);

    @Query("SELECT a FROM Album a JOIN a.sharedUsers su WHERE su.id = :userId")
    List<Album> findSharedAlbumsByUserId(@Param("userId") Long userId);

}
