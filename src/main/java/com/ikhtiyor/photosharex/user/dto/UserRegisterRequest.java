package com.ikhtiyor.photosharex.user.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest(
    @NotBlank
    String name,

    @Email
    String email,

    @NotBlank
    String password,

    @NotBlank
    @JsonProperty("profile_photo")
    String profilePhoto
) {

}
