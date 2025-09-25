package com.villaekinoks.app.configuration;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.security.Keys;

@Service
public class SecretKeyGenerator {

  private SecretKeyGenerator() {
  }

  public SecretKey generateAPISecretKey() {

    String secretKeyStr = "jwt_secret_key";
    return Keys.hmacShaKeyFor(secretKeyStr.getBytes(StandardCharsets.UTF_8));
  }
}
