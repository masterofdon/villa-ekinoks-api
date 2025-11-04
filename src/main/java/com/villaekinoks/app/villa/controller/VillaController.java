package com.villaekinoks.app.villa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.exception.BadApiRequestException;
import com.villaekinoks.app.exception.NotAuthorizedException;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseCodes;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.generic.entity.Address;
import com.villaekinoks.app.generic.service.AddressService;
import com.villaekinoks.app.propertyfacility.VillaFacility;
import com.villaekinoks.app.propertyfacility.service.VillaFacilityService;
import com.villaekinoks.app.user.SystemAdminUser;
import com.villaekinoks.app.user.VillaAdminUser;
import com.villaekinoks.app.user.VillaAdminUserRegistrationStatus;
import com.villaekinoks.app.user.service.VillaAdminUserService;
import com.villaekinoks.app.user.service.VillaOwnerRegistrationService;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.VillaFacilityItem;
import com.villaekinoks.app.villa.VillaPrivateInfo;
import com.villaekinoks.app.villa.VillaPublicInfo;
import com.villaekinoks.app.villa.response.Create_VillaFacilityItem_WC_MLS_XAction_Response;
import com.villaekinoks.app.villa.response.Create_Villa_WC_MLS_XAction_Response;
import com.villaekinoks.app.villa.response.Get_Villa_WC_MLS_XAction_Response;
import com.villaekinoks.app.villa.response.SimpleVillaFacilityItemView;
import com.villaekinoks.app.villa.response.Update_PricingRange_WC_MLS_XAction_Response;
import com.villaekinoks.app.villa.response.Update_Villa_PrivateInfo_WC_MLS_XAction_Response;
import com.villaekinoks.app.villa.response.Update_Villa_PublicInfo_WC_MLS_XAction_Response;
import com.villaekinoks.app.villa.service.VillaFacilityItemService;
import com.villaekinoks.app.villa.service.VillaService;
import com.villaekinoks.app.villa.xaction.Create_VillaFacilityItem_WC_MLS_XAction;
import com.villaekinoks.app.villa.xaction.Create_Villa_WC_MLS_XAction;
import com.villaekinoks.app.villa.xaction.Update_Villa_PrivateInfo_WC_MLS_XAction;
import com.villaekinoks.app.villa.xaction.Update_Villa_PublicInfo_WC_MLS_XAction;
import com.villaekinoks.app.villapricing.VillaPricingSchema;
import com.villaekinoks.app.villapricing.service.PricingRangeUtilService;
import com.villaekinoks.app.villapricing.service.VillaPricingSchemaService;
import com.villaekinoks.app.villapricing.xaction.Update_PricingRange_WC_MLS_XAction;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/villas")
@RequiredArgsConstructor
public class VillaController {

  private final VillaService villaService;

  private final VillaAdminUserService villaAdminUserService;

  private final VillaOwnerRegistrationService villaOwnerRegistrationService;

  private final VillaPricingSchemaService villaPricingSchemaService;

  private final PricingRangeUtilService pricingRangeUtilService;

  private final AddressService addressService;

  private final VillaFacilityItemService villaFacilityItemService;

  private final VillaFacilityService villaFacilityService;

  @GetMapping("/{id}")
  public GenericApiResponse<Get_Villa_WC_MLS_XAction_Response> getVillaById(@PathVariable String id) {
    Villa villa = this.villaService.getById(id);
    if (villa == null) {
      throw new NotFoundException(GenericApiResponseMessages.Generic.FAIL, "404#0011");
    }
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.VillaController.GET_VILLA_SUCCESS,
        new Get_Villa_WC_MLS_XAction_Response(villa));
  }

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

    villa = new Villa();

    VillaPublicInfo publicinfo = new VillaPublicInfo();
    publicinfo.setName(xAction.getName());
    publicinfo.setSlug(xAction.getSlug());
    publicinfo.setDescription(xAction.getDescription());
    publicinfo.setPromotext(xAction.getPromotext());
    publicinfo.setVilla(villa);

    VillaPrivateInfo privateinfo = new VillaPrivateInfo();
    if (xAction.getAddress() != null) {
      Address address = new Address();
      address.setStreet(xAction.getAddress().getStreet());
      address.setBuildingno(xAction.getAddress().getBuildingno());
      address.setCity(xAction.getAddress().getCity());
      address.setCounty(xAction.getAddress().getCounty());
      address.setCountry(xAction.getAddress().getCountry());
      address.setPostcode(xAction.getAddress().getPostcode());
      address.setLocation(xAction.getAddress().getLocation());
      address = this.addressService.create(address);

      privateinfo.setAddress(address);
    }
    privateinfo.setVilla(villa);

    villa.setPublicinfo(publicinfo);
    villa.setPrivateinfo(privateinfo);

    VillaPricingSchema pricing = new VillaPricingSchema();
    pricing.setVilla(villa);

    villa.setPricing(pricing);
    villa.setCreatedby(sysAdminUser);

    villa = this.villaService.create(villa);

    VillaAdminUser owner = this.villaAdminUserService.getByLogin(xAction.getOwneremail());
    if (owner == null) {
      if (xAction.getOwneremail() == null) {
        throw new BadApiRequestException("Owner Email is required", "400#0012");
      }
      owner = this.villaOwnerRegistrationService.startVillaOwnerRegistration(
          xAction.getOwneremail(),
          xAction.getOwnerfirstname(),
          xAction.getOwnermiddlename(),
          xAction.getOwnerlastname(),
          xAction.getOwnerdisplayname(),
          xAction.getOwnerphonenumber(),
          xAction.getOwneremail(),
          villa);

      owner.setRegistrationstatus(VillaAdminUserRegistrationStatus.CREATED);
      villa.setOwner(owner);
      villa = this.villaService.create(villa);
    }

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.VillaController.CREATE_VILLA_SUCCESS,
        new Create_Villa_WC_MLS_XAction_Response(villa.getId()));

  }

  @PutMapping("/{id}/public-info")
  public GenericApiResponse<Update_Villa_PublicInfo_WC_MLS_XAction_Response> updateVillaPublicInfo(
      @PathVariable String id,
      @RequestBody Update_Villa_PublicInfo_WC_MLS_XAction xAction) {

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!(principal instanceof VillaAdminUser)) {
      throw new NotAuthorizedException("User Not Authorized", "401#0002");
    }

    VillaAdminUser villaAdminUser = (VillaAdminUser) principal;

    if (villaAdminUser.getVilla().getId().equals(id) == false) {
      throw new NotAuthorizedException("User Not Authorized", "401#0002");
    }

    Villa villa = this.villaService.getById(id);
    if (villa == null) {
      throw new NotFoundException("Villa Not Found", "404#0011");
    }

    VillaPublicInfo publicinfo = villa.getPublicinfo();
    if (publicinfo == null) {
      publicinfo = new VillaPublicInfo();
      publicinfo.setSlug(xAction.getName());
      publicinfo.setVilla(villa);
      villa.setPublicinfo(publicinfo);
    }
    publicinfo.setName(xAction.getName());
    publicinfo.setPromotext(xAction.getPromotext());
    publicinfo.setDescription(xAction.getDescription());

    villa = this.villaService.create(villa);

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.VillaController.UPDATE_VILLA_SUCCESS,
        new Update_Villa_PublicInfo_WC_MLS_XAction_Response(villa.getId()));
  }

  @PutMapping("/{id}/private-info")
  public GenericApiResponse<Update_Villa_PrivateInfo_WC_MLS_XAction_Response> updateVillaPrivateinfo(
      @PathVariable String id, @RequestBody Update_Villa_PrivateInfo_WC_MLS_XAction xAction) {

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!(principal instanceof SystemAdminUser)) {
      throw new NotAuthorizedException("User Not Authorized", "401#0002");
    }

    Villa villa = this.villaService.getById(id);
    if (villa == null) {
      throw new NotFoundException("Villa Not Found", "404#0011");
    }
    VillaPrivateInfo privateinfo = villa.getPrivateinfo();

    if (xAction.getAddress() != null) {
      Address address = privateinfo.getAddress();
      if (address == null) {
        address = new Address();
      }
      address.setStreet(xAction.getAddress().getStreet());
      address.setBuildingno(xAction.getAddress().getBuildingno());
      address.setCity(xAction.getAddress().getCity());
      address.setCounty(xAction.getAddress().getCounty());
      address.setCountry(xAction.getAddress().getCountry());
      address.setPostcode(xAction.getAddress().getPostcode());
      address.setLocation(xAction.getAddress().getLocation());
      address = this.addressService.create(address);

      privateinfo.setAddress(address);
    }

    this.villaService.create(villa);

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.VillaController.UPDATE_VILLA_SUCCESS,
        new Update_Villa_PrivateInfo_WC_MLS_XAction_Response(villa.getId()));
  }

  @GetMapping("/{id}/pricing-schema")
  public GenericApiResponse<VillaPricingSchema> getVillaPricingSchema(@PathVariable String id) {

    Villa villa = this.villaService.getById(id);
    if (villa == null) {
      throw new NotFoundException("Villa Not Found", "404#0011");
    }

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.VillaController.GET_VILLA_PRICINGSCHEME_SUCCESS,
        villa.getPricing());
  }

  @PutMapping("/{id}/pricing-schema/pricing-ranges")
  public GenericApiResponse<Update_PricingRange_WC_MLS_XAction_Response> updatePricingRangeForVilla(
      @PathVariable String id,
      @RequestBody Update_PricingRange_WC_MLS_XAction xAction) {
    // Object principal =
    // SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // if (!(principal instanceof VillaAdminUser)) {
    // throw new NotAuthorizedException("User Not Authorized", "401#0002");
    // }

    // VillaAdminUser villaAdminUser = (VillaAdminUser) principal;

    // if (villaAdminUser.getVilla().getId().equals(id) == false) {
    // throw new NotAuthorizedException("User Not Authorized", "401#0002");
    // }

    Villa villa = this.villaService.getById(id);
    if (villa == null) {
      throw new NotFoundException("Villa Not Found", "404#0011");
    }

    VillaPricingSchema pricing = villa.getPricing();
    if (pricing == null) {
      pricing = new VillaPricingSchema();
      pricing.setVilla(villa);
      villa.setPricing(pricing);
      pricing = this.villaPricingSchemaService.create(pricing);
    }

    this.pricingRangeUtilService.processPricingRangeUpdate(pricing, xAction);

    // pricing = this.villaPricingSchemaService.create(pricing);

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.VillaController.UPDATE_VILLA_SUCCESS,
        new Update_PricingRange_WC_MLS_XAction_Response(pricing.getId()));
  }

  @GetMapping("/{id}/villa-facilities")
  public GenericApiResponse<Map<String, List<SimpleVillaFacilityItemView>>> getVillaFacilities(
      @PathVariable String id) {

    Villa villa = this.villaService.getById(id);
    if (villa == null) {
      throw new NotFoundException("Villa Not Found", "404#0011");
    }

    List<VillaFacilityItem> facilities = this.villaFacilityItemService.getAllByVillaId(id);
    if (facilities == null) {
      facilities = List.of();
    }

    List<VillaFacilityItem> sortedFacilities = facilities.stream().sorted(
        (a, b) -> a.getFacility().getCategory().getPriority().compareTo(b.getFacility().getCategory().getPriority()))
        .collect(Collectors.toList());

    Map<String, List<SimpleVillaFacilityItemView>> grouped = new HashMap<>();
    for (VillaFacilityItem item : sortedFacilities) {
      String categoryName = item.getFacility().getCategory().getName();
      if (grouped.containsKey(categoryName) == false) {
        grouped.put(categoryName, new java.util.ArrayList<>());
      }
      grouped.get(categoryName)
          .add(new SimpleVillaFacilityItemView(item.getFacility().getName(), item.getFacility().getDescription()));
    }

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.VillaController.GET_VILLA_FACILITIES_SUCCESS,
        grouped);
  }

  @PostMapping("/{id}/villa-facilities")
  @Transactional
  public GenericApiResponse<Create_VillaFacilityItem_WC_MLS_XAction_Response> createVillaFacilityItem(
      @PathVariable String id,
      @RequestBody Create_VillaFacilityItem_WC_MLS_XAction xAction) {

    Villa villa = this.villaService.getById(id);
    if (villa == null) {
      throw new NotFoundException("Villa Not Found", "404#0011");
    }

    // Validate input
    if (xAction.getVillafacilityids() == null || xAction.getVillafacilityids().isEmpty()) {
      throw new BadApiRequestException("Villa facility IDs list cannot be empty", "400#0021");
    }

    // Get existing facility items for this villa
    List<VillaFacilityItem> existingItems = this.villaFacilityItemService.getAllByVillaId(id);
    
    // Extract existing facility IDs for comparison
    List<String> existingFacilityIds = existingItems.stream()
        .map(item -> item.getFacility().getId())
        .collect(Collectors.toList());

    // Process new facility IDs
    List<String> newFacilityIds = xAction.getVillafacilityids();
    List<VillaFacilityItem> itemsToCreate = new java.util.ArrayList<>();
    
    for (String facilityId : newFacilityIds) {
      // Skip if facility is already selected for this villa
      if (existingFacilityIds.contains(facilityId)) {
        continue;
      }

      // Validate that the facility exists
      VillaFacility facility = this.villaFacilityService.getById(facilityId);
      if (facility == null) {
        throw new NotFoundException("Villa Facility Not Found with ID: " + facilityId, "404#0020");
      }

      // Create new facility item
      VillaFacilityItem facilityItem = new VillaFacilityItem();
      facilityItem.setVilla(villa);
      facilityItem.setFacility(facility);
      itemsToCreate.add(facilityItem);
    }

    // Find items to delete (exist in DB but not in new list)
    List<VillaFacilityItem> itemsToDelete = existingItems.stream()
        .filter(item -> !newFacilityIds.contains(item.getFacility().getId()))
        .collect(Collectors.toList());

    // Create new facility items
    for (VillaFacilityItem item : itemsToCreate) {
      this.villaFacilityItemService.create(item);
    }

    // Delete items that are no longer in the list
    if (!itemsToDelete.isEmpty()) {
      this.villaFacilityItemService.deleteAll(itemsToDelete);
    }

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        GenericApiResponseCodes.VillaController.CREATE_VILLA_FACILITY_ITEM_SUCCESS,
        new Create_VillaFacilityItem_WC_MLS_XAction_Response("Processed " + itemsToCreate.size() + " new items, deleted " + itemsToDelete.size() + " items"));
  }
}
