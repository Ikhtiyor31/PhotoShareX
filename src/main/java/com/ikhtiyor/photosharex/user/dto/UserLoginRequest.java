package com.ikhtiyor.photosharex.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
    @NotBlank(message = "email field cannot be null or empty")
    String email,

    @NotBlank(message = "password field cannot be null or empty")
    String password
){}
