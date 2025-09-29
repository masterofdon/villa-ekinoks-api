package com.villaekinoks.app.booking.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.booking.VillaBooking;
import com.villaekinoks.app.booking.VillaBookingGuest;
import com.villaekinoks.app.booking.VillaBookingGuestPersonalInfo;
import com.villaekinoks.app.booking.VillaBookingStatus;
import com.villaekinoks.app.booking.VillaBookingTimestamps;
import com.villaekinoks.app.booking.response.Create_VillaBooking_WC_MLS_XAction_Response;
import com.villaekinoks.app.booking.service.VillaBookingGuestService;
import com.villaekinoks.app.booking.service.VillaBookingService;
import com.villaekinoks.app.booking.view.VillaBookingSummaryView;
import com.villaekinoks.app.booking.xaction.Create_VillaBooking_WC_MLS_XAction;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.payment.PaymentRequest;
import com.villaekinoks.app.payment.service.PaymentProcessingService;
import com.villaekinoks.app.payment.view.PaymentRequestWCView;
import com.villaekinoks.app.utils.TimeUtils;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/villa-bookings")
@RequiredArgsConstructor
public class VillaBookingController {

  private final VillaBookingService villaBookingService;

  private final VillaBookingGuestService villaBookingGuestService;

  private final VillaService villaService;

  private final PaymentProcessingService paymentProcessingService;

  @GetMapping
  public GenericApiResponse<Page<VillaBookingSummaryView>> getVillaBookings(
      @RequestParam String villaid, Pageable pageable) {
    List<VillaBookingSummaryView> bookings = villaBookingService.getAll(new String[] { villaid }, pageable).stream()
        .map(VillaBookingSummaryView::fromEntity).collect(Collectors.toList());

    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), bookings.size());
    Page<VillaBookingSummaryView> bookingsPage = new PageImpl<>(bookings.subList(start, end), pageable,
        bookings.size());
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#40591",
        bookingsPage);
  }

  @PostMapping
  @Transactional
  public GenericApiResponse<Create_VillaBooking_WC_MLS_XAction_Response> createVillaBooking(
      @RequestBody Create_VillaBooking_WC_MLS_XAction xAction) {

    Villa villa = this.villaService.getById(xAction.getVillaid());
    if (villa == null) {
      throw new NotFoundException();
    }

    VillaBooking booking = new VillaBooking();
    booking.setVilla(villa);
    booking.setStartdate(xAction.getStartdate());
    booking.setEnddate(xAction.getEnddate());
    booking.setStatus(VillaBookingStatus.PENDING);

    VillaBookingTimestamps timestamps = new VillaBookingTimestamps();
    timestamps.setCreationdate(TimeUtils.tsInstantNow().toEpochMilli());
    timestamps.setBooking(booking);

    booking.setTimestamps(timestamps);

    booking = this.villaBookingService.create(booking);

    VillaBookingGuest inquiror = new VillaBookingGuest();
    VillaBookingGuestPersonalInfo personalInfo = new VillaBookingGuestPersonalInfo();
    personalInfo.setFirstname(xAction.getInquiror_firstname());
    personalInfo.setMiddlename(xAction.getInquiror_middlename());
    personalInfo.setLastname(xAction.getInquiror_lastname());
    personalInfo.setEmail(xAction.getInquiror_email());
    personalInfo.setPhonenumber(xAction.getInquiror_phonenumber());
    personalInfo.setGuest(inquiror);
    inquiror.setPersonalinfo(personalInfo);
    inquiror.setBooking(booking);

    inquiror = this.villaBookingGuestService.create(inquiror);

    booking.setInquiror(inquiror);

    booking = this.villaBookingService.create(booking);

    Create_VillaBooking_WC_MLS_XAction_Response response = new Create_VillaBooking_WC_MLS_XAction_Response();
    response.setId(booking.getId());

    PaymentRequest paymentRequest = this.paymentProcessingService.createPaymentRequestForBooking(
        booking.getId(),
        inquiror.getPersonalinfo().getEmail(),
        "19491591",
        "3000.00",
        "EUR",
        0,
        0);
    response.setPaymentrequest(PaymentRequestWCView.fromEntity(paymentRequest));

    return new GenericApiResponse<>(
        HttpStatus.CREATED.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "201#40592",
        response);
  }
}
