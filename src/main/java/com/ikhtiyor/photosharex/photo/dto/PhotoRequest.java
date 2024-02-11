package com.ikhtiyor.photosharex.photo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PhotoRequest(
    @NotBlank
    @JsonProperty("image_url")
    String imageUrl,

    @NotBlank
    String title,

    @NotBlank
    String description,

    @NotNull
    @JsonProperty("visibility_type")
    VisibilityType visibilityType,

    @NotBlank
    String location
) {

}
