package com.ikhtiyor.photosharex.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessTokenServiceTest {

    @InjectMocks
    private AccessTokenServiceImpl accessTokenService;


    @Test
    void shouldCreateAccessToken_WhenEmailValid() {
        String email = "fake@example.com";
        var token = accessTokenService.createAccessToken(email);
        assertNotNull(token.getAccess());
        assertNotNull(token.getRefresh());
        assertFalse(token.getAccess().isEmpty());
        assertTrue(accessTokenService.isValidToken(token.getAccess()));
    }

    @Test
    void shouldGenerateInvalidToken_WhenEmail_isEmpty() {
        var token = accessTokenService.createAccessToken("");
        String userEmail = accessTokenService.extractUserEmail(token.getAccess());
        assertTrue(userEmail.isEmpty());
    }

    @Test
    void shouldGenerateInvalidToken_WhenEmail_Null() {
        var token = accessTokenService.createAccessToken(null);
        assertThrows(NullPointerException.class, () -> accessTokenService.extractUserEmail(token.getAccess()));
    }

    @Test
    void extractUserEmail() {
        String email = "mynew@gmail.com";
        var token = accessTokenService.createAccessToken(email);
        String extractEmailFromAccessToken = accessTokenService.extractUserEmail(token.getAccess());
        String extractEmailFromRefreshToken = accessTokenService.extractUserEmail(token.getRefresh());
        assertFalse(extractEmailFromRefreshToken.isEmpty());
        assertFalse(extractEmailFromAccessToken.isEmpty());
        assertFalse(extractEmailFromAccessToken.isBlank());
        assertFalse(extractEmailFromRefreshToken.isBlank());
        assertThat(extractEmailFromAccessToken).isEqualTo(extractEmailFromRefreshToken);
    }

    @Test
    void isValidToken() {
        String email = "test@gmail.com";
        var token = accessTokenService.createAccessToken(email);
        var isAccessTokenValid = accessTokenService.isValidToken(token.getAccess());
        var isRefreshTokenValid = accessTokenService.isValidToken(token.getRefresh());
        assertTrue(isAccessTokenValid);
        assertTrue(isRefreshTokenValid);
    }
}