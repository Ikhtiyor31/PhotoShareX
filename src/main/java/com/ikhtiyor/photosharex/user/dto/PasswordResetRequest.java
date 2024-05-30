package com.ikhtiyor.photosharex.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
    @Email(message = "email field cannot be null or empty")
    String email,
    @NotBlank(message = "old_password field cannot be null or empty")
    @JsonProperty("old_password")
    String oldPassword,

    @NotBlank(message = "new_password field cannot be null or empty")
    @Size(min = 6, max = 15, message = "password must be between 6 and 15 characters")
    @JsonProperty("new_password")
    String newPassword
) {

}