package com.ikhtiyor.photosharex.photo.dto;

import com.ikhtiyor.photosharex.photo.model.Album;

public record AlbumDTO(
    String title,
    String description,
    String coverImageUrl
) {
    public static AlbumDTO from(Album album) {
        return new AlbumDTO(
            album.getTitle(),
            album.getDescription(),
            album.getCoverImageUrl()
        );
    }
}
