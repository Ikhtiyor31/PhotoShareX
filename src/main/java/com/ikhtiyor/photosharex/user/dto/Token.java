package com.ikhtiyor.photosharex.user.dto;

public class Token {

    private String access;
    private String refresh;

    public Token(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    public static Token of(String access, String refresh) {
        return new Token(access, refresh);
    }

    public String getAccess() {
        return access;
    }

    public String getRefresh() {
        return refresh;
    }
}
