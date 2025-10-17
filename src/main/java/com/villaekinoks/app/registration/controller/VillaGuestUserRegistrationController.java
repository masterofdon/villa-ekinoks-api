package com.villaekinoks.app.registration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.exception.BadApiRequestException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.mail.service.AsyncEmailService;
import com.villaekinoks.app.registration.response.Create_VillaGuestUserRegistrationVerification_WC_MLS_XAction_Response;
import com.villaekinoks.app.registration.response.Create_VillaGuestUser_WC_MLS_XAction_Response;
import com.villaekinoks.app.registration.xaction.Create_VillaGuestUserRegistrationVerification_WC_MLS_XAction;
import com.villaekinoks.app.registration.xaction.Create_VillaGuestUser_WC_MLS_XAction;
import com.villaekinoks.app.user.LockStatus;
import com.villaekinoks.app.user.OperationStatus;
import com.villaekinoks.app.user.ServiceStatus;
import com.villaekinoks.app.user.VerificationStatus;
import com.villaekinoks.app.user.VillaGuestUser;
import com.villaekinoks.app.user.service.VillaGuestUserRegistrationService;
import com.villaekinoks.app.user.service.VillaGuestUserService;
import com.villaekinoks.app.utils.RandomizerUtils;
import com.villaekinoks.app.utils.TimeUtils;
import com.villaekinoks.app.verification.VerificationPair;
import com.villaekinoks.app.verification.VerificationPairStatus;
import com.villaekinoks.app.verification.service.VerificationPairService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user-registrations/villa-guest-users")
@RequiredArgsConstructor
public class VillaGuestUserRegistrationController {

  private final VillaGuestUserRegistrationService villaGuestUserRegistrationService;

  private final VillaGuestUserService villaGuestUserService;

  private final VerificationPairService verificationPairService;

  private final AsyncEmailService asyncEmailService;

  @PostMapping
  @Transactional
  public GenericApiResponse<Create_VillaGuestUser_WC_MLS_XAction_Response> createVillaGuestUser(
      @RequestBody Create_VillaGuestUser_WC_MLS_XAction xAction) {

    VillaGuestUser vGuestUser = this.villaGuestUserService.getByLogin(xAction.getEmail());
    if (vGuestUser == null) {
      vGuestUser = this.villaGuestUserRegistrationService.registerNewUser(
          xAction.getEmail(),
          xAction.getEmail(),
          xAction.getFirstname(),
          xAction.getMiddlename(),
          xAction.getLastname(),
          xAction.getIdentitynumber(),
          xAction.getEmail(),
          xAction.getPhonenumber(),
          xAction.getLocale(),
          xAction.getCurrency());
    }

    VerificationPair vPair = new VerificationPair();
    vPair.setUserid(vGuestUser.getId());
    vPair.setCreationdate(TimeUtils.tsInstantNow().toEpochMilli());
    vPair.setExpirationdate(TimeUtils.tsInstantNow().toEpochMilli() + 24 * 60 * 60 * 1000); // 24 hours validity
    vPair.setVerificationcode(RandomizerUtils.getRandomNumeric(6));
    vPair.setVerificationdomain("guestuserregistration");
    vPair.setStatus(VerificationPairStatus.PENDING);
    vPair = this.verificationPairService.create(vPair);

    // Send verification email asynchronously
    this.asyncEmailService.sendGuestUserRegistrationVerificationEmailAsync(
        vGuestUser,
        vPair,
        xAction.getLocale());

    return new GenericApiResponse<>(
        HttpStatus.CREATED.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "201#98513",
        new Create_VillaGuestUser_WC_MLS_XAction_Response(vPair.getId()));
  }

  @PostMapping("/verify")
  public GenericApiResponse<Create_VillaGuestUserRegistrationVerification_WC_MLS_XAction_Response> verifyVillaGuestUserRegistration(
      Create_VillaGuestUserRegistrationVerification_WC_MLS_XAction xAction) {

    VerificationPair vPair = this.verificationPairService.getById(xAction.getRegistrationverificationid());
    if (vPair == null) {
      throw new BadApiRequestException("Invalid verification ID.", "400#0020");
    }
    if (vPair.getUserid() == null) {
      throw new BadApiRequestException("Invalid verification ID.", "400#0020");
    }
    VillaGuestUser vGuestUser = this.villaGuestUserService.getById(vPair.getUserid());
    if (vGuestUser == null) {
      throw new BadApiRequestException("Invalid verification ID.", "400#0020");
    }
    if (!vGuestUser.getId().equals(vPair.getUserid())) {
      throw new BadApiRequestException("Invalid verification ID.", "400#0020");
    }
    if (!vPair.getVerificationcode().equals(xAction.getCode())) {
      throw new BadApiRequestException("Invalid verification code.", "400#0021");
    }
    vGuestUser.getStatusset().setLockstatus(LockStatus.UNLOCKED);
    vGuestUser.getStatusset().setOperationstatus(OperationStatus.ENABLED);
    vGuestUser.getStatusset().setServicestatus(ServiceStatus.ACTIVE);
    vGuestUser.getStatusset().setVerificationstatus(VerificationStatus.VERIFIED);
    vGuestUser.getTimestamps().setVerificationdate(TimeUtils.tsInstantNow().toEpochMilli());
    vGuestUser = this.villaGuestUserService.create(vGuestUser);

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#98813",
        new Create_VillaGuestUserRegistrationVerification_WC_MLS_XAction_Response(vGuestUser));
  }
}
