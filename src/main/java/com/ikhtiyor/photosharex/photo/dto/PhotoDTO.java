package com.ikhtiyor.photosharex.photo.dto;

import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.photo.model.Photo;

public record PhotoDTO(
    Long id,
    String imageUrl,
    String title,
    String description,
    VisibilityType visibilityType,
    String location
) {

    public static PhotoDTO from(Photo photo) {
        return new PhotoDTO(
            photo.getId(),
            photo.getImageUrl(),
            photo.getTitle(),
            photo.getDescription(),
            photo.getVisibilityType(),
            photo.getLocation()
        );
    }
}
