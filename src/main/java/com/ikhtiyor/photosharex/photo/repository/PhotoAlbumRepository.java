package com.ikhtiyor.photosharex.photo.repository;

import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.photo.model.PhotoAlbum;
import com.ikhtiyor.photosharex.photo.model.PhotoAlbumId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, PhotoAlbumId> {

    Optional<PhotoAlbum> findPhotoAlbumByPhotoAlbumId(PhotoAlbumId photoAlbumId);

    Page<PhotoAlbum> findPhotoAlbumsByAlbum_Id(Long albumId, Pageable pageable);

    @Query("SELECT pa.album FROM PhotoAlbum pa WHERE pa.photo.id = :photoId")
    List<Album> findAlbumsByPhotoId(@Param("photoId") Long photoId);

    @Query("SELECT pa.photo FROM PhotoAlbum pa WHERE pa.album.id = :albumId")
    List<Photo> findPhotosByAlbumId(@Param("albumId") Long albumId);

    @Modifying
    @Query("DELETE FROM PhotoAlbum pa WHERE pa.album.id = :albumId")
    void deleteAllByAlbumId(@Param("albumId") Long albumId);

    boolean existsByPhotoIdAndAlbumId(Long photoId, Long albumId);
}
