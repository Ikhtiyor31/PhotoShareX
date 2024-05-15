package com.ikhtiyor.photosharex.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerificationCodeRequest(
    @NotBlank(message = "email field cannot be null or empty")
    @Email(message = "invalid email address")
    String email,

    @NotBlank(message = "email field cannot be null or empty")
    @JsonProperty("verification_code")
    String verificationCode
) {

}
