package com.villaekinoks.app.propertyfacility.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.propertyfacility.VillaFacility;
import com.villaekinoks.app.propertyfacility.VillaFacilityCategory;
import com.villaekinoks.app.propertyfacility.response.Create_VillaFacility_WC_MLS_XAction_Response;
import com.villaekinoks.app.propertyfacility.service.VillaFacilityCategoryService;
import com.villaekinoks.app.propertyfacility.service.VillaFacilityService;
import com.villaekinoks.app.propertyfacility.xaction.Create_VillaFacility_WC_MLS_XAction;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/villa-facilities")
@RequiredArgsConstructor
public class VillaFacilityController {

  private final VillaFacilityService villaFacilityService;

  private final VillaFacilityCategoryService villaFacilityCategoryService;

  @PostMapping
  public GenericApiResponse<Create_VillaFacility_WC_MLS_XAction_Response> createVillaFacility(
      @RequestBody Create_VillaFacility_WC_MLS_XAction xAction) {

    VillaFacilityCategory category = this.villaFacilityCategoryService.getById(xAction.getCategoryid());
    if (category == null) {
      throw new NotFoundException();
    }

    VillaFacility facility = new VillaFacility();
    facility.setName(xAction.getName());
    facility.setDescription(xAction.getDescription());
    facility.setCategory(category);
    facility.setPriority(xAction.getPriority());
    facility = villaFacilityService.create(facility);

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "201#74813",
        new Create_VillaFacility_WC_MLS_XAction_Response(facility.getId()));
  }
}
