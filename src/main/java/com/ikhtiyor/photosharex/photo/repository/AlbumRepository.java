package com.ikhtiyor.photosharex.photo.repository;

import com.ikhtiyor.photosharex.photo.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

}
