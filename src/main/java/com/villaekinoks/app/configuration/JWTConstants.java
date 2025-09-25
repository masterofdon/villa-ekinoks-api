package com.villaekinoks.app.configuration;

public class JWTConstants {
    private JWTConstants() {
    }

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 24 * 60 * 10;
    public static final String SECRET_KEY = "jwt_secret_key";
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 24 * 60 * 1;
}
