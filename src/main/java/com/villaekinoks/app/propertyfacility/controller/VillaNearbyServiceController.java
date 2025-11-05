package com.villaekinoks.app.propertyfacility.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.propertyfacility.VillaNearbyService;
import com.villaekinoks.app.propertyfacility.response.Create_VillaNearbyService_WC_MLS_XAction_Response;
import com.villaekinoks.app.propertyfacility.service.VillaNearbyServiceService;
import com.villaekinoks.app.propertyfacility.xaction.Create_VillaNearbyService_WC_MLS_XAction;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/villa-nearby-services")
@RequiredArgsConstructor
public class VillaNearbyServiceController {

  private final VillaNearbyServiceService villaNearbyServiceService;

  private final VillaService villaService;

  @GetMapping
  public GenericApiResponse<List<VillaNearbyService>> getAllVillaNearbyServices(
      @RequestParam String villaid) {
    List<VillaNearbyService> services = villaNearbyServiceService.getByVillaId(villaid);
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#891033",
        services);
  }

  @PostMapping
  public GenericApiResponse<Create_VillaNearbyService_WC_MLS_XAction_Response> createVillaNearbyService(
      @RequestBody Create_VillaNearbyService_WC_MLS_XAction xAction) {

    Villa villa = villaService.getById(xAction.getVillaid());
    if (villa == null) {
      throw new NotFoundException(
          "Villa with id " + xAction.getVillaid() + " not found.");
    }
    VillaNearbyService xService = new VillaNearbyService();
    xService.setVilla(villa);
    xService.setType(xAction.getType());
    xService.setName(xAction.getName());
    xService.setDistance(xAction.getDistance());
    xService.setLocation(xAction.getLocation());
    VillaNearbyService created = villaNearbyServiceService.create(xService);
    Create_VillaNearbyService_WC_MLS_XAction_Response response = new Create_VillaNearbyService_WC_MLS_XAction_Response();
    response.setId(created.getId());
    return new GenericApiResponse<>(
        HttpStatus.CREATED.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "201#019814",
        response);
  }
}
