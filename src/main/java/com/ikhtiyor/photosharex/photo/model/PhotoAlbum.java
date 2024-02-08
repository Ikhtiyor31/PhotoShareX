package com.ikhtiyor.photosharex.photo.model;

import com.ikhtiyor.photosharex.AuditableEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "photo_albums")
public class PhotoAlbum extends AuditableEntity {

    @EmbeddedId
    private PhotoAlbumId photoAlbumId;

    @ManyToOne
    @MapsId("photoId")
    @JoinColumn(
        name = "photo_id",
        foreignKey = @ForeignKey(
            name = "photo_album_photo_id_fk"
        )
    )
    private Photo photo;

    @ManyToOne
    @MapsId("albumId")
    @JoinColumn(
        name = "album_id",
        foreignKey = @ForeignKey(
            name = "photo_album_album_id_fk"
        )
    )
    private Album album;

    public PhotoAlbum(PhotoAlbumId photoAlbumId, Photo photo, Album album) {
        this.photoAlbumId = photoAlbumId;
        this.photo = photo;
        this.album = album;
    }

    public PhotoAlbum(Photo photo, Album album) {
        this.photo = photo;
        this.album = album;
    }

    public PhotoAlbumId getPhotoAlbumId() {
        return photoAlbumId;
    }

    public void setPhotoAlbumId(PhotoAlbumId photoAlbumId) {
        this.photoAlbumId = photoAlbumId;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
