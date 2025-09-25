package com.villaekinoks.app.appauthenticator.service;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

@Service
public class AppAuthenticatorService {

  private static final Path CODE_PATH = Path.of("/opt/villa-ekinoks-authenticator/code");

  public String getCode() {
    try {
      return Files.readString(CODE_PATH).trim();
    } catch (Exception e) {
      throw new RuntimeException("Cannot read authenticator code", e);
    }
  }
}
