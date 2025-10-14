package com.villaekinoks.app.stats.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.configuration.annotation.VillaEkinoksAuthorized;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.stats.VillaStat;
import com.villaekinoks.app.stats.response.Get_VillaStats_WC_MLS_XAction_Response;
import com.villaekinoks.app.stats.service.VillaStatService;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/villa-stats")
@RequiredArgsConstructor
public class VillaStatController {

  private final VillaService villaService;

  private final VillaStatService villaStatService;

  @GetMapping
  @VillaEkinoksAuthorized
  public GenericApiResponse<Get_VillaStats_WC_MLS_XAction_Response> getVillaStats(
      @RequestParam String villaid) {

    Villa villa = villaService.getById(villaid);
    if (villa == null) {
      throw new NotFoundException();
    }
    List<VillaStat> stats = villaStatService.getByVillaId(villaid);
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#09491",
        new Get_VillaStats_WC_MLS_XAction_Response(stats));
  }
}
