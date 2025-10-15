package com.villaekinoks.app.availability.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.discount.DiscountCode;
import com.villaekinoks.app.discount.DiscountCodeStatus;
import com.villaekinoks.app.discount.service.DiscountCodeService;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/discount-code-checker")
@RequiredArgsConstructor
public class DiscountCodeAvailability {

  private final DiscountCodeService discountCodeService;

  private final VillaService villaService;

  @GetMapping
  public GenericApiResponse<DiscountCode> checkDiscountCode(@RequestParam String villaid, @RequestParam String code) {

    Villa villa = villaService.getById(villaid);
    if (villa == null) {
      throw new NotFoundException();
    }

    DiscountCode discountCode = discountCodeService.getByCodeAndVillaId(code, villaid);
    if (discountCode == null || discountCode.getStatus() != DiscountCodeStatus.ACTIVE) {
      throw new NotFoundException();
    }
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#4151",
        discountCode);
  }
}
