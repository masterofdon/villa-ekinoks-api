package com.villaekinoks.app.servicableitem.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.servicableitem.ServicableItem;
import com.villaekinoks.app.servicableitem.response.Create_ServicableItem_WC_MLS_XAction_Response;
import com.villaekinoks.app.servicableitem.service.ServicableItemService;
import com.villaekinoks.app.servicableitem.xaction.Create_ServiceableItem_WC_MLS_XAction;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/servicable-items")
@RequiredArgsConstructor
public class ServicableItemController {

  private final ServicableItemService servicableItemService;

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
  public GenericApiResponse<Create_ServicableItem_WC_MLS_XAction_Response> createServicableItem(
      @RequestBody Create_ServiceableItem_WC_MLS_XAction xAction) {

    ServicableItem item = new ServicableItem();
    item.setName(xAction.getName());
    item.setDescription(xAction.getDescription());
    item.setIconlink(xAction.getIconlink());
    item.setUnit(xAction.getUnit());
    item.setPrice(xAction.getPrice());

    ServicableItem createdItem = this.servicableItemService.create(item);
    Create_ServicableItem_WC_MLS_XAction_Response response = new Create_ServicableItem_WC_MLS_XAction_Response();
    response.setId(createdItem.getId());

    return new GenericApiResponse<>(
        HttpStatus.CREATED.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#091841",
        response);
  }
}
