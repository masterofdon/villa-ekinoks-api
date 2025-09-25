package com.villaekinoks.app.villa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.exception.BadApiRequestException;
import com.villaekinoks.app.exception.NotAuthorizedException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseCodes;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.user.SystemAdminUser;
import com.villaekinoks.app.user.VillaAdminUser;
import com.villaekinoks.app.user.service.VillaAdminUserService;
import com.villaekinoks.app.user.service.VillaOwnerRegistrationService;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.VillaPrivateInfo;
import com.villaekinoks.app.villa.VillaPublicInfo;
import com.villaekinoks.app.villa.response.Create_Villa_WC_MLS_XAction_Response;
import com.villaekinoks.app.villa.service.VillaService;
import com.villaekinoks.app.villa.xaction.Create_Villa_WC_MLS_XAction;
import com.villaekinoks.app.villapricing.VillaPricingSchema;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/villas")
@RequiredArgsConstructor
public class VillaController {

  private final VillaService villaService;

  private final VillaAdminUserService villaAdminUserService;

  private final VillaOwnerRegistrationService villaOwnerRegistrationService;

  @PostMapping
  @Transactional
  public GenericApiResponse<Create_Villa_WC_MLS_XAction_Response> createVilla(
      @RequestBody Create_Villa_WC_MLS_XAction xAction) {

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!(principal instanceof SystemAdminUser)) {
      throw new NotAuthorizedException("User Not Authorized", "401#0002");
    }
    SystemAdminUser sysAdminUser = (SystemAdminUser) principal;

    Villa villa = this.villaService.getBySlug(xAction.getSlug());
    if (villa != null) {
      throw new BadApiRequestException("Villa Already Exists", "400#0010");
    }

    VillaAdminUser owner = this.villaAdminUserService.getByLogin(xAction.getOwneremail());
    if (owner == null) {
      owner = this.villaOwnerRegistrationService.startVillaOwnerRegistration(
          xAction.getOwneremail(),
          xAction.getOwnerfirstname(),
          xAction.getOwnermiddlename(),
          xAction.getOwnerlastname(),
          xAction.getOwnerdisplayname(),
          xAction.getOwnerphonenumber(),
          xAction.getOwneremail());
    }

    villa = new Villa();

    VillaPublicInfo publicinfo = new VillaPublicInfo();
    publicinfo.setName(xAction.getName());
    publicinfo.setSlug(xAction.getSlug());
    publicinfo.setDescription(xAction.getDescription());
    publicinfo.setVilla(villa);

    VillaPrivateInfo privateinfo = new VillaPrivateInfo();
    privateinfo.setAddress(xAction.getAddress());
    privateinfo.setVilla(villa);

    villa.setPublicinfo(publicinfo);
    villa.setPrivateinfo(privateinfo);
    villa.setOwner(owner);

    VillaPricingSchema pricing = new VillaPricingSchema();
    pricing.setVilla(villa);

    villa.setPricing(pricing);
    villa.setCreatedby(sysAdminUser);

    villa = this.villaService.create(villa);

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.VillaController.CREATE_VILLA_SUCCESS,
        new Create_Villa_WC_MLS_XAction_Response(villa.getId()));

  }
}
