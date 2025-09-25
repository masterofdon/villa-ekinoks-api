package com.villaekinoks.app.configuration;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Profile("production")
public class ApplicationCorsConfigurationProduction implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOrigins(
            "https://www.villaekinoks.com",
            "https://villaekinoks.com",
            "http://localhost:8080",
            "http://localhost:5173")
        .allowedMethods("GET", "POST", "PUT", "PATCH", "OPTIONS", "DELETE");
  }

}
