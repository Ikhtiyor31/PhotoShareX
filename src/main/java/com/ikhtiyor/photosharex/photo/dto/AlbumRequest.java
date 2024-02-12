package com.ikhtiyor.photosharex.photo.dto;

import jakarta.validation.constraints.NotBlank;

public record AlbumRequest(

    @NotBlank(message = "title field cannot be null or empty")
    String title,

    @NotBlank(message = "description field cannot be null or empty")
    String description
) {

}
