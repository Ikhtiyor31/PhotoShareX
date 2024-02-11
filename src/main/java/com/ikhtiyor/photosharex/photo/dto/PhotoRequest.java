package com.ikhtiyor.photosharex.photo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PhotoRequest(
    @NotBlank(message = "image_url field cannot be null or empty")
    @JsonProperty("image_url")
    String imageUrl,

    @NotBlank(message = "title field cannot be null or empty")
    String title,

    @NotBlank(message = "description field cannot be null or empty")
    String description,

    @NotNull(message = "visibility_type field cannot null or empty")
    @JsonProperty("visibility_type")
    VisibilityType visibilityType,

    @NotBlank(message = "location field cannot null or empty")
    String location
) {

}
