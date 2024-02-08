package com.ikhtiyor.photosharex.photo.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PhotoAlbumId implements Serializable {

    @Column(name = "photo_id", nullable = false)
    private Long photoId;

    @Column(name = "album_id", nullable = false)
    private Long albumId;

    public PhotoAlbumId() {}
    public PhotoAlbumId(Long photoId, Long albumId) {
        this.photoId = photoId;
        this.albumId = albumId;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhotoAlbumId that = (PhotoAlbumId) o;
        return Objects.equals(photoId, that.photoId) && Objects.equals(albumId,
            that.albumId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoId, albumId);
    }
}
