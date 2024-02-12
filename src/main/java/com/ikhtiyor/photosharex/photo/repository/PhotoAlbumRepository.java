package com.ikhtiyor.photosharex.photo.repository;

import com.ikhtiyor.photosharex.photo.model.PhotoAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {

}
