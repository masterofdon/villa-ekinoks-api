package com.villaekinoks.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private static final String baseUrl = "/api/v1";

  private final JwtAuthorizationFilter jwtAuthorizationFilter;

  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth -> {
              auth.requestMatchers(
                  baseUrl + "/oauth/**",
                  baseUrl + "/app-files/o/**",
                  baseUrl + "/verification-pair-controller/**",
                  baseUrl + "/villa-calendars/**",
                  baseUrl + "/villa-bookings/**",
                  baseUrl + "/servicable-items/**",
                  baseUrl + "/villas/**",
                  baseUrl + "/price-checker/check-item-prices/**",
                  baseUrl + "/availability-check-controller/check-villa-availability/**",
                  baseUrl + "/user-registrations/system-admin-users/**",
                  baseUrl + "/app-users/forgot-password/**",
                  baseUrl + "/app-users/forget-password/verifications/**").permitAll();
              auth.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();
              auth.anyRequest().authenticated();
            })
        .sessionManagement(management -> management
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(e -> e
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.PROXY_AUTHENTICATION_REQUIRED)));

    return http.build();
  }
}
