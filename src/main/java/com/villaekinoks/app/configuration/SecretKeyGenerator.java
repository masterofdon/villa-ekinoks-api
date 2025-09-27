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

    String secretKeyStr = JWTConstants.SECRET_KEY;
    return Keys.hmacShaKeyFor(secretKeyStr.getBytes(StandardCharsets.UTF_8));
  }
}
