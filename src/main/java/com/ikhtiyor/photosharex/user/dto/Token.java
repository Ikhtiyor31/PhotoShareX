package com.ikhtiyor.photosharex.user.dto;

public record Token(String access, String refresh) {

    public static Token of(String access, String refresh) {
        return new Token(access, refresh);
    }
}
