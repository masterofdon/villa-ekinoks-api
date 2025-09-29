package com.villaekinoks.app.registration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.exception.BadApiRequestException;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseCodes;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.registration.response.Post_VillaOwnerUser_SetPasswordFirstTime_XAction_Response;
import com.villaekinoks.app.registration.xaction.Post_VillaOwnerUser_SetPasswordFirstTime_XAction;
import com.villaekinoks.app.user.VillaAdminUser;
import com.villaekinoks.app.user.service.VillaAdminUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user-registrations/villa-admin-users")
@RequiredArgsConstructor
public class VillaAdminUserRegistrationController {

  private final VillaAdminUserService villaAdminUserService;

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @PostMapping("/set-password-first-time")
  public GenericApiResponse<Post_VillaOwnerUser_SetPasswordFirstTime_XAction_Response> setPasswordFirstTime(
      Post_VillaOwnerUser_SetPasswordFirstTime_XAction xAction) {

    VillaAdminUser user = villaAdminUserService.getByLogin(xAction.getOwneruserlogin());
    if (user == null) {
      throw new NotFoundException();
    }

    if (xAction.getNewpassword() == null || xAction.getNewpassword().isBlank()) {
      throw new NotFoundException("New password is required", "404#3001");
    }

    if (!xAction.getNewpassword().equals(xAction.getConfirmpassword())) {
      throw new BadApiRequestException("New password and confirm password do not match", "400#3002");
    }

    user.setPassword(bCryptPasswordEncoder.encode(xAction.getNewpassword()));
    user = villaAdminUserService.create(user);

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.VillaOwnerUserRegistrationController.UPDATE_VILLAOWNERUSER_PASSWORD_SUCCESS,
        new Post_VillaOwnerUser_SetPasswordFirstTime_XAction_Response(user.getId()));
  }
}
