package com.villaekinoks.app.verification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.configuration.JwtService;
import com.villaekinoks.app.exception.NotAuthorizedException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.generic.model.TokenizedUser;
import com.villaekinoks.app.user.AppUser;
import com.villaekinoks.app.user.service.AppUserService;
import com.villaekinoks.app.verification.VerificationPair;
import com.villaekinoks.app.verification.VerificationPairStatus;
import com.villaekinoks.app.verification.service.VerificationPairService;
import com.villaekinoks.app.verification.xaction.Verify_LoginVerification_XAction;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/verification-pair-controller")
@RequiredArgsConstructor
@Transactional
public class VerificationPairController {

  private final VerificationPairService verificationPairService;

  private final AppUserService appUserService;

  private final JwtService jwtService;

  @PostMapping("/login-verifications")
  public GenericApiResponse<TokenizedUser> verifyLoginVerification(
      @RequestBody Verify_LoginVerification_XAction xAction) {

    VerificationPair vPair = this.verificationPairService.getById(xAction.getRequestid());
    if (vPair == null) {
      throw new NotAuthorizedException(
          GenericApiResponseMessages.Generic.FAIL,
          "401#9002");
    }

    if (vPair.getVerificationcode().equals(xAction.getCode()) == false) {
      throw new NotAuthorizedException(
          GenericApiResponseMessages.Generic.FAIL,
          "401#9005");
    }

    AppUser aUser = this.appUserService.getById(vPair.getUserid());
    if (aUser == null) {
      throw new NotAuthorizedException(
          GenericApiResponseMessages.Generic.FAIL,
          "401#9002");
    }

    if (vPair.getStatus() != null
        && vPair.getStatus().equals(VerificationPairStatus.PENDING) == false) {
      throw new NotAuthorizedException(
          GenericApiResponseMessages.Generic.FAIL,
          "401#9003");
    }

    if (vPair.getExpirationdate() < System.currentTimeMillis()) {
      throw new NotAuthorizedException(
          GenericApiResponseMessages.Generic.FAIL,
          "401#9004");
    }

    vPair.setStatus(VerificationPairStatus.VERIFIED);
    this.verificationPairService.create(vPair);

    TokenizedUser tokenizedUser = new TokenizedUser();
    tokenizedUser.setUser(aUser);
    tokenizedUser.setAccesstoken(jwtService.generateAccessToken(aUser));
    tokenizedUser.setRefreshtoken(jwtService.generateRefreshToken(aUser));

    return new GenericApiResponse<TokenizedUser>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#919411",
        tokenizedUser);
  }
}
