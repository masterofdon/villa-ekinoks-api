package com.villaekinoks.app.availability.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.availability.response.Post_ItemPricesBreakdown_Response;
import com.villaekinoks.app.availability.view.PriceItem;
import com.villaekinoks.app.availability.xaction.Post_ItemPricesBreakdown_XAction;
import com.villaekinoks.app.booking.xaction.Create_VillaBookingAdditionalService_WC_MLS_XAction;
import com.villaekinoks.app.exception.BadApiRequestException;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.generic.entity.Price;
import com.villaekinoks.app.servicableitem.ServicableItem;
import com.villaekinoks.app.servicableitem.service.ServicableItemService;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;
import com.villaekinoks.app.villapricing.PricingRange;
import com.villaekinoks.app.villapricing.service.PricingRangeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/price-checker")
@RequiredArgsConstructor
public class PriceCheckerController {

  private final VillaService villaService;

  private final PricingRangeService pricingRangeService;

  private final ServicableItemService servicableItemService;

  @PostMapping("/check-item-prices")
  public GenericApiResponse<Post_ItemPricesBreakdown_Response> checkItemPrices(
      @RequestBody Post_ItemPricesBreakdown_XAction xAction) {

    if (xAction.getVillaid() == null) {
      throw new BadApiRequestException();
    }

    Villa villa = this.villaService.getById(xAction.getVillaid());
    if (villa == null) {
      throw new NotFoundException(
          GenericApiResponseMessages.Generic.FAIL,
          "404#7001");
    }

    String startdate = xAction.getStartdate();
    String enddate = xAction.getEnddate();

    LocalDate startDate = LocalDate.parse(startdate, DateTimeFormatter.ofPattern("yyyyMMdd"));
    LocalDate endDate = LocalDate.parse(enddate, DateTimeFormatter.ofPattern("yyyyMMdd"));

    List<PricingRange> allDates = new ArrayList<>();
    for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
      String currDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
      PricingRange pricingRange = this.pricingRangeService.getVillaPriceInDate(xAction.getVillaid(), currDate);
      allDates.add(pricingRange);
    }
    BigDecimal totalAmount = allDates.stream().reduce(BigDecimal.ZERO,
        (sum, pr) -> sum.add(new BigDecimal(pr.getPricepernight().getAmount())), BigDecimal::add);
    totalAmount = totalAmount.setScale(2);
    BigDecimal rentAmount = new BigDecimal(totalAmount.toString());
    // For additional services check all services inside the request
    ArrayList<PriceItem> services = new ArrayList<>();
    services.add(new PriceItem(
        "Rent",
        allDates.size(),
        "night",
        new Price(rentAmount.toString(), xAction.getCurrency())));

    if (xAction.getAdditionalservices() != null) {
      for (Create_VillaBookingAdditionalService_WC_MLS_XAction item : xAction.getAdditionalservices()) {
        ServicableItem sItem = this.servicableItemService.getById(item.getServicableitemid());
        if (sItem != null) {
          BigDecimal itemTotal = new BigDecimal(sItem.getPrice().getAmount())
              .multiply(new BigDecimal(item.getQuantity()));
          itemTotal = itemTotal.setScale(2);
          totalAmount = totalAmount
              .add(itemTotal);
          services.add(new PriceItem(
              sItem.getName(),
              item.getQuantity(),
              sItem.getUnit(),
              new Price(itemTotal.toString(), xAction.getCurrency())));
        }
      }
    }

    Post_ItemPricesBreakdown_Response response = new Post_ItemPricesBreakdown_Response();
    response.setItems(services);

    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#0000",
        response);
  }
}
