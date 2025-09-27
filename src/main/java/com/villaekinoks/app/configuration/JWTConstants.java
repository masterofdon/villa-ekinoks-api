package com.villaekinoks.app.configuration;

public class JWTConstants {
    private JWTConstants() {
    }

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 24 * 60 * 10;
    // 256 bits = 32 bytes (characters if using ASCII)
    public static final String SECRET_KEY = "b7f8c3e2a1d4f6b9e5c7a8d2f3b6c9e1a4d7f2b5c8e3a6d9f1b4c7e2a5d8f3b6c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6";
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 24 * 60 * 1;
}
