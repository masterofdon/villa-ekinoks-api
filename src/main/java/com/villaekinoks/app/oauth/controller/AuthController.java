package com.villaekinoks.app.oauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.authentication.response.AppUserLogin_WC_MLS_XAction_Response;
import com.villaekinoks.app.authentication.xaction.AppUserLogin_WC_MLS_XAction;
import com.villaekinoks.app.exception.NotAuthorizedException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.mail.service.LoginVerificationCodeEmailService;
import com.villaekinoks.app.user.AppUser;
import com.villaekinoks.app.user.service.AppUserService;
import com.villaekinoks.app.utils.RandomizerUtils;
import com.villaekinoks.app.utils.TimeUtils;
import com.villaekinoks.app.verification.VerificationPair;
import com.villaekinoks.app.verification.VerificationPairStatus;
import com.villaekinoks.app.verification.service.VerificationPairService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
public class AuthController {

  private final AppUserService appUserService;

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  private final VerificationPairService verificationPairService;

  private final LoginVerificationCodeEmailService loginVerificationCodeEmailService;

  @PostMapping("/login")
  public GenericApiResponse<AppUserLogin_WC_MLS_XAction_Response> login(
      @RequestBody AppUserLogin_WC_MLS_XAction xAction,
      HttpServletRequest request) {

    AppUser appUser = this.appUserService.getByLogin(xAction.getLogin());

    if (appUser == null || this.bCryptPasswordEncoder.matches(xAction.getPassword(),
        appUser.getPassword()) == false) {
      AppUserLogin_WC_MLS_XAction_Response response = new AppUserLogin_WC_MLS_XAction_Response();
      response.setAckid("FAIL");
      response.setRequestid("401#9001");
      throw new NotAuthorizedException(
          GenericApiResponseMessages.Generic.FAIL,
          "401#9001");
    }

    VerificationPair vPair = new VerificationPair();
    vPair.setUserid(appUser.getId());
    vPair.setCreationdate(TimeUtils.tsInstantNow().toEpochMilli());
    vPair.setExpirationdate(TimeUtils.tsInstantNow().toEpochMilli() + (5 * 60 * 1000)); // 5 minutes
    vPair.setStatus(VerificationPairStatus.PENDING);
    vPair.setVerificationcode(RandomizerUtils.getRandomNumeric(6));

    vPair = this.verificationPairService.create(vPair);

    // Send verification code email asynchronously
    try {
      this.loginVerificationCodeEmailService.sendVerificationCodeEmail(appUser, vPair.getVerificationcode(), request);
    } catch (Exception e) {
      // Log the error but don't fail the login process
      // The login was successful, email sending is just a notification
    }

    AppUserLogin_WC_MLS_XAction_Response response = new AppUserLogin_WC_MLS_XAction_Response();
    response.setAckid("SUCCESS");
    response.setRequestid(vPair.getId());

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#919410",
        response);
  }
}
