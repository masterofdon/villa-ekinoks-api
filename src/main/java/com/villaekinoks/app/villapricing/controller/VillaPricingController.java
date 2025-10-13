package com.villaekinoks.app.villapricing.controller;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.booking.VillaBooking;
import com.villaekinoks.app.booking.service.VillaBookingService;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;
import com.villaekinoks.app.villapricing.VillaPricingSchema;
import com.villaekinoks.app.villapricing.response.BookingInfo;
import com.villaekinoks.app.villapricing.response.SimpleVillaBooking;
import com.villaekinoks.app.villapricing.response.VillaPricingSchemaWithBookingInfo;
import com.villaekinoks.app.villapricing.response.VillaPricingWithVillaBooking;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/villa-pricing")
@RequiredArgsConstructor
public class VillaPricingController {

  private final VillaService villaService;

  private final VillaBookingService villaBookingService;

  @GetMapping
  public GenericApiResponse<VillaPricingSchemaWithBookingInfo> getVillaPricingSchema(@RequestParam String villaid) {

    Villa villa = this.villaService.getById(villaid);
    if (villa == null) {
      throw new NotFoundException();
    }
    VillaPricingSchema pricingSchema = villa.getPricing();
    if (pricingSchema == null) {
      throw new NotFoundException();
    }

    VillaPricingSchemaWithBookingInfo pricingSchemaWithBookings = new VillaPricingSchemaWithBookingInfo();
    pricingSchemaWithBookings.setPricing(pricingSchema);
    pricingSchemaWithBookings.setBookings(new ArrayList<>());
    Page<VillaBooking> bookings = villaBookingService.getByVillaId(villaid, Pageable.unpaged());
    bookings.getContent().forEach(e -> {
      BookingInfo bookingInfo = new BookingInfo();
      bookingInfo.setStartdate(e.getStartdate());
      bookingInfo.setEnddate(e.getEnddate());
      pricingSchemaWithBookings.getBookings().add(bookingInfo);
    });
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#VILLA01",
        pricingSchemaWithBookings);
  }

  @GetMapping("/detailed")
  public GenericApiResponse<VillaPricingWithVillaBooking> getVillaPricingSchemaDetailed(
      @RequestParam String villaid) {

    Villa villa = this.villaService.getById(villaid);
    if (villa == null) {
      throw new NotFoundException();
    }
    VillaPricingSchema pricingSchema = villa.getPricing();
    if (pricingSchema == null) {
      throw new NotFoundException();
    }

    VillaPricingWithVillaBooking pricingSchemaWithBookings = new VillaPricingWithVillaBooking();
    pricingSchemaWithBookings.setPricing(pricingSchema);
    pricingSchemaWithBookings.setBookings(new ArrayList<>());
    Page<VillaBooking> bookings = villaBookingService.getByVillaId(villaid, Pageable.unpaged());
    bookings.getContent().forEach(e -> {
      SimpleVillaBooking bookingInfo = new SimpleVillaBooking();
      bookingInfo.setStartdate(e.getStartdate());
      bookingInfo.setEnddate(e.getEnddate());
      bookingInfo.setId(e.getId());
      bookingInfo.setStatus(e.getStatus());
      bookingInfo.setInquiror(e.getInquiror());
      pricingSchemaWithBookings.getBookings().add(bookingInfo);
    });
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#VILLA01",
        pricingSchemaWithBookings);
  }
}
