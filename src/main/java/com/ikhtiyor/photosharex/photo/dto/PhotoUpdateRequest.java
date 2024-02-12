package com.ikhtiyor.photosharex.photo.dto;

import jakarta.validation.constraints.NotBlank;

public record PhotoUpdateRequest(
    @NotBlank(message = "title field cannot be null or empty")
    String title,

    @NotBlank(message = "description field cannot be null or empty")
    String description,

    @NotBlank(message = "location field cannot be null or empty")
    String location
) {

}
