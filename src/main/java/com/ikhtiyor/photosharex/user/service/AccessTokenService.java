package com.ikhtiyor.photosharex.user.service;

import com.ikhtiyor.photosharex.user.dto.Token;

public interface AccessTokenService {
    Token createAccessToken(String email);
    String extractUserEmail(String token);
    boolean isValidToken(String token);
}
