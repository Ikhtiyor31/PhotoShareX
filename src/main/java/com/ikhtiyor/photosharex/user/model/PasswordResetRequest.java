package com.ikhtiyor.photosharex.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(
    @Email
    String email,
    @NotBlank
    @JsonProperty("old_password") String oldPassword,

    @NotBlank
    @JsonProperty("new_password") String newPassword) {

}