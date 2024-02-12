package com.ikhtiyor.photosharex.photo.dto;

import jakarta.validation.constraints.Min;
import java.util.List;

public class AddPhotosToAlbumRequest {

    @Min(value = 1, message = "albumId field cannot be null or empty")
    private Long albumId;

    @Min(value = 1, message = "At least one photoId must be provided")
    private List<@Min(value = 1, message = "photoId field cannot be null or empty") Long> photoIds;

    // Getters and setters
    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public List<Long> getPhotoIds() {
        return photoIds;
    }

    public void setPhotoIds(List<Long> photoIds) {
        this.photoIds = photoIds;
    }
}
