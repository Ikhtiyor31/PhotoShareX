package com.ikhtiyor.photosharex.user.service;

public interface AccessTokenService {
    String createAccessToken(String email);
    String createRefreshToken(String email);
    String extractUsername(String token);
    boolean isValidToken(String token);
}
