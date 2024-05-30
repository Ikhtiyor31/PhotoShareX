package com.ikhtiyor.photosharex.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerificationCodeRequest(
    @NotBlank(message = "email field cannot be null or empty")
    @Email(message = "invalid email address")
    String email,

    @NotBlank(message = "verification_code field cannot be null or empty")
    @Size(min = 4, max = 4)
    @JsonProperty("verification_code")
    String verificationCode
) {

}
