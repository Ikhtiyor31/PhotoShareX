package com.ikhtiyor.photosharex.user.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
    @NotBlank(message = "name field cannot be null or empty")
    String name,

    @NotBlank(message = "email field cannot be null or empty")
    @Email(message = "invalid email address")
    String email,

    @NotNull(message = "password field cannot be null or empty")
    @Size(min = 6, max = 15, message = "password must be between 6 to 15 characters")
    String password,

    @NotBlank(message = "profile_photo field cannot be null or empty")
    @JsonProperty("profile_photo")
    String profilePhoto
) {

}
