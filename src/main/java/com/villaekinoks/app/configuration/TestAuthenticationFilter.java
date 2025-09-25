package com.villaekinoks.app.configuration;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.villaekinoks.app.authentication.xaction.AppUserLogin_WC_MLS_XAction;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseCodes;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.generic.entity.VerificationToken;
import com.villaekinoks.app.user.AppUser;
import com.villaekinoks.app.user.service.AppUserService;
import com.villaekinoks.app.utils.RandomizerUtils;
import com.villaekinoks.app.utils.TimeUtils;
import com.villaekinoks.app.verification.VerificationPair;
import com.villaekinoks.app.verification.VerificationPairStatus;
import com.villaekinoks.app.verification.service.VerificationPairService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private AuthenticationManager authenticationManager;

  private VerificationPairService verificationPairService;

  private AppUserService appUserService;

  private static final Logger logger = LogManager.getLogger(TestAuthenticationFilter.class);

  public TestAuthenticationFilter(
      String loginUrl,
      AuthenticationManager authenticationManager,
      VerificationPairService verificationPairService,
      AppUserService appUserService) {
    this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginUrl));
    this.authenticationManager = authenticationManager;
    this.verificationPairService = verificationPairService;
    this.appUserService = appUserService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    final ObjectMapper mapper = new ObjectMapper();
    AppUser aUser = null;
    try {

      AppUserLogin_WC_MLS_XAction creds = mapper.readValue(request.getInputStream(),
          AppUserLogin_WC_MLS_XAction.class);
      AppUser user = new AppUser();
      user.setPassword(creds.getPassword());
      user.setLogin(creds.getLogin());

      aUser = this.appUserService.getByLogin(creds.getLogin());
      if (aUser != null) {
        user.setRole(aUser.getRole());
      }

      return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
          user.getUsername(), user.getPassword(), user.getAuthorities()));
    } catch (IOException e) {
      throw new InternalAuthenticationServiceException(e.getMessage());
    } catch (DisabledException e) {
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      GenericApiResponse<Object> apiRepsonse = new GenericApiResponse<>(403,
          GenericApiResponseMessages.Generic.FAIL,
          GenericApiResponseCodes.AuthenticationResponseCodes.USER_DISABLED);
      try {
        mapper.writeValue(response.getOutputStream(), apiRepsonse);
      } catch (IOException e1) {
      }
      throw new DisabledException(e.getMessage());
    } catch (LockedException e) {
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      GenericApiResponse<Object> apiRepsonse = new GenericApiResponse<>(
          HttpStatus.FORBIDDEN.value(),
          GenericApiResponseMessages.Generic.FAIL,
          GenericApiResponseCodes.AuthenticationResponseCodes.USER_ACCOUNT_LOCKED);
      try {
        mapper.writeValue(response.getOutputStream(), apiRepsonse);
      } catch (IOException e1) {
      }
      throw new LockedException(e.getMessage());
    } catch (BadCredentialsException e) {
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);

      GenericApiResponse<Object> apiRepsonse = new GenericApiResponse<>(
          HttpStatus.FORBIDDEN.value(),
          GenericApiResponseMessages.Generic.FAIL,
          GenericApiResponseCodes.AuthenticationResponseCodes.BAD_CREDENTIALS);

      try {
        mapper.writeValue(response.getOutputStream(), apiRepsonse);
      } catch (IOException e1) {
      }
      throw new BadCredentialsException(e.getMessage());
    } catch (CredentialsExpiredException e) {
      throw new CredentialsExpiredException(e.getMessage());
    } catch (Exception e) {
      throw new InternalAuthenticationServiceException(e.getMessage());
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authResult) throws IOException, ServletException {
    AppUser user = (AppUser) authResult.getPrincipal();

    if (user == null) {
      return;
    }

    VerificationPair verification = new VerificationPair();
    verification.setStatus(VerificationPairStatus.PENDING);
    verification.setCreationdate(TimeUtils.tsInstantNow().toEpochMilli());
    verification.setExpirationdate(TimeUtils.tsInstantNow().plusMillis(2L * 60L * 1000L).toEpochMilli());
    String emailVer = RandomizerUtils.getRandomNumeric(6);

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("Verification : %s", emailVer));
    }

    verification.setVerificationcode(emailVer);
    verification.setUserid(user.getId());
    verification = this.verificationPairService.create(verification);

    VerificationToken verToken = new VerificationToken();
    verToken.setVerificationid(verification.getId());
    verToken.setCode(emailVer);

    // try {
    // Create_LoginVerificationMain_XAction xAction =
    // CreateLoginVerificationMailXAction.builder()
    // .displayname(user.getPersonalinfo().getDisplayname())
    // .toMail(user.getPersonalinfo().getEmail())
    // .verificationcode(emailVer)
    // .build();

    // this.emailSenderClient.sendLoginVerificationMail(xAction);
    // } catch (Exception e) {

    // }

    ObjectMapper mapper = new ObjectMapper();
    response.getOutputStream().write(mapper.writeValueAsBytes(verToken));
    response.setContentType("application/json");
    response.setStatus(200);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed) throws IOException, ServletException {
    super.unsuccessfulAuthentication(request, response, failed);
  }

}
