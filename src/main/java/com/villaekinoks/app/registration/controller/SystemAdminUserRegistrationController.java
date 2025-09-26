package com.villaekinoks.app.registration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.appauthenticator.service.AppAuthenticatorService;
import com.villaekinoks.app.exception.BadApiRequestException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseCodes;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.registration.response.Create_SystemAdminUser_WC_MLS_XAction_Response;
import com.villaekinoks.app.registration.xaction.Create_SystemAdminUser_WC_MLS_XAction;
import com.villaekinoks.app.user.SystemAdminUser;
import com.villaekinoks.app.user.service.SystemAdminUserRegistrationService;
import com.villaekinoks.app.user.service.SystemAdminUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user-registrations/system-admin-users")
@RequiredArgsConstructor
@Transactional
public class SystemAdminUserRegistrationController {

  private final SystemAdminUserRegistrationService systemAdminUserRegistrationService;
  private final SystemAdminUserService systemAdminUserService;

  private final AppAuthenticatorService appAuthenticatorService;

  @PostMapping
  public GenericApiResponse<Create_SystemAdminUser_WC_MLS_XAction_Response> createSystemAdminUser(
      @RequestBody Create_SystemAdminUser_WC_MLS_XAction xAction) {

    if (xAction.getSecret() == null || this.appAuthenticatorService.getCode().equals(xAction.getSecret()) == false) {
      throw new BadApiRequestException(
          GenericApiResponseMessages.Generic.FAIL,
          "400#8002");
    }

    SystemAdminUser existingUser = systemAdminUserService.getByLogin(xAction.getEmail());
    if (existingUser != null) {
      throw new BadApiRequestException(
          GenericApiResponseMessages.Generic.FAIL,
          "400#8001");
    }

    SystemAdminUser savedUser = systemAdminUserRegistrationService.registerSystemAdminUser(
        xAction.getEmail(),
        xAction.getPassword(),
        xAction.getFirstname(),
        xAction.getMiddlename(),
        xAction.getLastname(),
        xAction.getFirstname() + " " + xAction.getLastname(),
        xAction.getPhonenumber(),
        xAction.getEmail());

    return new GenericApiResponse<>(
        HttpStatus.CREATED.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.SystemAdminUserRegistrationController.CREATE_USER_SUCCESS,
        new Create_SystemAdminUser_WC_MLS_XAction_Response(
            savedUser.getId(),
            "System admin user created successfully"));
  }
}
