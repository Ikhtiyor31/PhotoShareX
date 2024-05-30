package com.ikhtiyor.photosharex.user.dto;

public record AccessTokenDTO(
    String accessToken,
    String refreshToken
) {
}
