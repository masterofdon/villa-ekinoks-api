package com.villaekinoks.app.propertyfacility.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.propertyfacility.VillaFacilityCategory;
import com.villaekinoks.app.propertyfacility.response.Create_VillaFacilityCategory_WC_MLS_XAction_Response;
import com.villaekinoks.app.propertyfacility.service.VillaFacilityCategoryService;
import com.villaekinoks.app.propertyfacility.xaction.Create_VillaFacilityCategory_WC_MLS_XAction;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/villa-facility-categories")
@RequiredArgsConstructor
public class VillaFacilityCategoryController {

  private final VillaFacilityCategoryService villaFacilityCategoryService;

  @PostMapping
  public GenericApiResponse<Create_VillaFacilityCategory_WC_MLS_XAction_Response> createVillaFacilityCategory(
      @RequestBody Create_VillaFacilityCategory_WC_MLS_XAction xAction) {

    VillaFacilityCategory category = new VillaFacilityCategory();
    category.setName(xAction.getName());
    category.setPriority(xAction.getPriority());
    category = villaFacilityCategoryService.create(category);
    return new GenericApiResponse<>(
        HttpStatus.CREATED.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "201#49143",
        new Create_VillaFacilityCategory_WC_MLS_XAction_Response(category.getId()));
  }
}
