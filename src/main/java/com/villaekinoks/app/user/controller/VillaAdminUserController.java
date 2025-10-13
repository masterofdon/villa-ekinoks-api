package com.villaekinoks.app.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.configuration.annotation.VillaEkinoksAuthorized;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.user.VillaAdminUser;
import com.villaekinoks.app.user.response.Reset_VillaAdminUserPassword_WC_MLS_XAction_Response;
import com.villaekinoks.app.user.service.VillaAdminUserService;
import com.villaekinoks.app.user.xaction.Reset_VillaAdminUserPassword_WC_MLS_XAction;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/villa-admin-users")
@RequiredArgsConstructor
public class VillaAdminUserController {

  private final VillaAdminUserService villaAdminUserService;

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @PostMapping("/reset-password")
  @VillaEkinoksAuthorized
  public GenericApiResponse<Reset_VillaAdminUserPassword_WC_MLS_XAction_Response> resetPassword(
      Reset_VillaAdminUserPassword_WC_MLS_XAction xAction) {

    VillaAdminUser user = this.villaAdminUserService.getByLogin(xAction.getEmail());
    user.setPassword(bCryptPasswordEncoder.encode(xAction.getNewpassword()));

    user = this.villaAdminUserService.create(user);

    Reset_VillaAdminUserPassword_WC_MLS_XAction_Response response = new Reset_VillaAdminUserPassword_WC_MLS_XAction_Response();
    response.setId(user.getId());
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#945914",
        response);
  }
}
