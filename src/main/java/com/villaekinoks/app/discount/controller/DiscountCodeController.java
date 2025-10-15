package com.villaekinoks.app.discount.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.configuration.annotation.VillaEkinoksAuthorized;
import com.villaekinoks.app.discount.DiscountCode;
import com.villaekinoks.app.discount.DiscountCodeStatus;
import com.villaekinoks.app.discount.response.Create_DiscountCode_WC_MLS_XAction_Response;
import com.villaekinoks.app.discount.response.Get_DiscountCode_WC_MLS_XAction_Response;
import com.villaekinoks.app.discount.service.DiscountCodeService;
import com.villaekinoks.app.discount.xaction.Create_DiscountCode_WC_MLS_XAction;
import com.villaekinoks.app.exception.NotAuthorizedException;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.user.VillaAdminUser;
import com.villaekinoks.app.utils.RandomizerUtils;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/discount-codes")
@RequiredArgsConstructor
public class DiscountCodeController {

  private final VillaService villaService;

  private final DiscountCodeService discountCodeService;

  @GetMapping
  @VillaEkinoksAuthorized
  public GenericApiResponse<Get_DiscountCode_WC_MLS_XAction_Response> getAll(@RequestParam String villaid) {

    List<DiscountCode> codes = this.discountCodeService.getAll(villaid);

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#019491",
        new Get_DiscountCode_WC_MLS_XAction_Response(codes));
  }

  @PostMapping
  @VillaEkinoksAuthorized
  public GenericApiResponse<Create_DiscountCode_WC_MLS_XAction_Response> create(
      Create_DiscountCode_WC_MLS_XAction xAction) {

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof VillaAdminUser sUser) {

      Villa villa = this.villaService.getById(xAction.getVillaid());
      if (villa == null) {
        throw new NotFoundException();
      }

      if (sUser.getVilla() == null || !sUser.getVilla().getId().equals(villa.getId())) {
        throw new NotAuthorizedException();
      }

      DiscountCode code = new DiscountCode();
      code.setCode(RandomizerUtils.getRandomNumeric(6));
      code.setVilla(villa);
      code.setCreatedby(sUser);
      code.setDiscounttype(xAction.getType());
      code.setValue(xAction.getValue());
      code.setUsagetype(xAction.getUsagetype());
      code.setStatus(DiscountCodeStatus.ACTIVE);
      code = this.discountCodeService.create(code);
    }

    throw new NotAuthorizedException();
  }
}
