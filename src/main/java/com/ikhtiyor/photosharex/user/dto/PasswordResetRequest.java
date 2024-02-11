package com.ikhtiyor.photosharex.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(
    @Email(message = "email field cannot be null or empty")
    String email,
    @NotBlank(message = "old_password field cannot be null or empty")
    @JsonProperty("old_password")
    String oldPassword,

    @NotBlank(message = "new_password field cannot be null or empty")
    @JsonProperty("new_password")
    String newPassword
) {

}