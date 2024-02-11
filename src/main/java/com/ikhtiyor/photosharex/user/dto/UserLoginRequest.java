package com.ikhtiyor.photosharex.user.dto;

public record UserLoginRequest(
    String email,
    String password
){}
