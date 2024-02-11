package com.ikhtiyor.photosharex.user.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest(
    @NotBlank(message = "name field cannot be null or empty")
    String name,

    @NotBlank(message = "email field cannot be null or empty")
    @Email(message = "invalid email address")
    String email,

    @NotBlank(message = "password field cannot be null or empty")
    String password,

    @NotBlank(message = "profile_photo field cannot be null or empty")
    @JsonProperty("profile_photo")
    String profilePhoto
) {

}
