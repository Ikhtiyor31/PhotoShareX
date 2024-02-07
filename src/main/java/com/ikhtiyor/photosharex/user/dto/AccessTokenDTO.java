package com.ikhtiyor.photosharex.user.dto;

public class AccessTokenDTO {

    private String accessToken;
    private String refreshToken;

    public AccessTokenDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
