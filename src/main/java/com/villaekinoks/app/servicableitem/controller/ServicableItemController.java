package com.villaekinoks.app.servicableitem.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.configuration.annotation.VillaEkinoksAuthorized;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.servicableitem.ServicableItem;
import com.villaekinoks.app.servicableitem.response.Create_ServicableItem_WC_MLS_XAction_Response;
import com.villaekinoks.app.servicableitem.response.Update_ServicableItemStatus_WC_MLS_XAction_Response;
import com.villaekinoks.app.servicableitem.service.ServicableItemService;
import com.villaekinoks.app.servicableitem.xaction.Create_ServiceableItem_WC_MLS_XAction;
import com.villaekinoks.app.servicableitem.xaction.Update_ServicableItemStatus_WC_MLS_XAction;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/servicable-items")
@RequiredArgsConstructor
public class ServicableItemController {

  private final ServicableItemService servicableItemService;

  private final VillaService villaService;

  @GetMapping
  public GenericApiResponse<Page<ServicableItem>> getAllServicableItems(
      @RequestParam String villaid,
      Pageable pageable) {
    Page<ServicableItem> items = this.servicableItemService.getAll(villaid, pageable);
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "951951",
        items);
  }

  @PostMapping
  @VillaEkinoksAuthorized
  public GenericApiResponse<Create_ServicableItem_WC_MLS_XAction_Response> createServicableItem(
      @RequestBody Create_ServiceableItem_WC_MLS_XAction xAction) {

    if (xAction.getVillaid() == null) {
      throw new NotFoundException(
          "Villa ID must be provided");
    }

    Villa villa = this.villaService.getById(xAction.getVillaid());
    if (villa == null) {
      throw new NotFoundException(
          "Villa not found");
    }

    ServicableItem item = new ServicableItem();
    item.setName(xAction.getName());
    item.setDescription(xAction.getDescription());
    item.setIconlink(xAction.getIconlink());
    item.setVilla(villa);
    item.setUnit(xAction.getUnit());
    item.setPrice(xAction.getPrice());
    item.setMaximum(xAction.getMaximum());
    item.setMinimum(xAction.getMinimum());

    ServicableItem createdItem = this.servicableItemService.create(item);
    Create_ServicableItem_WC_MLS_XAction_Response response = new Create_ServicableItem_WC_MLS_XAction_Response();
    response.setId(createdItem.getId());

    return new GenericApiResponse<>(
        HttpStatus.CREATED.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#091841",
        response);
  }

  @PutMapping("/{id}/status")
  public GenericApiResponse<Update_ServicableItemStatus_WC_MLS_XAction_Response> updateServicableItemStatus(
      @PathVariable String id,
      @RequestBody Update_ServicableItemStatus_WC_MLS_XAction xAction) {
    ServicableItem item = this.servicableItemService.getById(id);
    if (item == null) {
      throw new NotFoundException(
          "Servicable item not found");
    }
    item.setStatus(xAction.getStatus());
    ServicableItem updatedItem = this.servicableItemService.create(item);
    Update_ServicableItemStatus_WC_MLS_XAction_Response response = new Update_ServicableItemStatus_WC_MLS_XAction_Response();
    response.setId(updatedItem.getId());

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#091841",
        response);
  }

  @DeleteMapping("/{id}")
  @VillaEkinoksAuthorized
  public GenericApiResponse<Void> deleteServicableItem(@PathVariable String id) {
    ServicableItem item = this.servicableItemService.getById(id);
    if (item == null) {
      throw new NotFoundException(
          "Servicable item not found");
    }

    this.servicableItemService.delete(item);

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#091842",
        null);
  }
}
