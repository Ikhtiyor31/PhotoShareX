package com.ikhtiyor.photosharex.photo.repository;

import com.ikhtiyor.photosharex.photo.model.PhotoAlbum;
import com.ikhtiyor.photosharex.photo.model.PhotoAlbumId;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, PhotoAlbumId> {

    Optional<PhotoAlbum> findPhotoAlbumByPhotoAlbumId(PhotoAlbumId photoAlbumId);

    Page<PhotoAlbum> findPhotoAlbumsByAlbum_Id(Long albumId, Pageable pageable);
}
