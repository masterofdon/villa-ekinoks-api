package com.villaekinoks.app.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.villaekinoks.app.authentication.service.UserLoginService;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private static final String baseUrl = "/api/v1";

  private final List<String> excludeUrls = Arrays.asList(
      baseUrl + "/oauth/**",
      baseUrl + "/app-files/o/**",
      baseUrl + "/external-invoice-root-requests/**",
      baseUrl + "/verification-pair-controller/**",
      baseUrl + "/external-token-generator-controller/**",
      baseUrl + "/app-users/forgot-password/**",
      baseUrl + "/app-users/forget-password/verifications/**");

  private final JwtService jwtService;

  private final UserLoginService userDetailsService;

  @Override
  protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
      @Nonnull FilterChain filterChain)
      throws ServletException, IOException {

    String requestURI = request.getRequestURI();

    // Skip filter if the request URL matches any of the excluded URLs
    if (excludeUrls.stream().anyMatch(requestURI::startsWith)) {
      filterChain.doFilter(request, response);
      return;
    }

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }
    final String authHeader = request.getHeader("Authorization");
    String jwt = null;
    String userLogin = null;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      jwt = authHeader.substring(7);
      userLogin = jwtService.extractLogin(jwt);
    } catch (Exception e) {
      response.setStatus(HttpStatus.GONE.value());
      return;
    }

    if (userLogin != null && jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userLogin);

      if (userDetails != null) {
        if (!(userDetails.isAccountNonExpired() && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired() && userDetails.isEnabled())) {
          response.setStatus(HttpStatus.GONE.value());
          return;
        }
        if (jwtService.isTokenValid(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails,
              null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
          response.setStatus(HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value());
        }
      } else {
        response.setStatus(HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value());
      }

    }
    filterChain.doFilter(request, response);

  }

}
