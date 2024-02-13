package com.ikhtiyor.photosharex.photo.dto;

import jakarta.validation.constraints.Min;
import java.util.List;

public record PhotoIdsRequest(

    @Min(value = 1, message = "At least one photoId must be provided")
    List<@Min(value = 1, message = "photoId field cannot be null or empty") Long> photoIds
) {

}
