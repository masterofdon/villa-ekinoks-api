package com.villaekinoks.app.booking.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.booking.VillaBooking;
import com.villaekinoks.app.booking.VillaBookingAdditionalService;
import com.villaekinoks.app.booking.VillaBookingStatus;
import com.villaekinoks.app.booking.VillaBookingTimestamps;
import com.villaekinoks.app.booking.response.Create_BookingPayment_WC_MLS_XAction_Response;
import com.villaekinoks.app.booking.response.Create_VillaBooking_WC_MLS_XAction_Response;
import com.villaekinoks.app.booking.service.VillaBookingAdditionalServiceService;
import com.villaekinoks.app.booking.service.VillaBookingService;
import com.villaekinoks.app.booking.view.VillaBookingSummaryView;
import com.villaekinoks.app.booking.xaction.Create_BookingPayment_WC_MLS_XAction;
import com.villaekinoks.app.booking.xaction.Create_VillaBookingAdditionalService_WC_MLS_XAction;
import com.villaekinoks.app.booking.xaction.Create_VillaBooking_WC_MLS_XAction;
import com.villaekinoks.app.configuration.annotation.VillaEkinoksAuthorized;
import com.villaekinoks.app.exception.BadApiRequestException;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.payment.Payment;
import com.villaekinoks.app.payment.PaymentStatus;
import com.villaekinoks.app.payment.service.IyzicoPaymentProcessingService;
import com.villaekinoks.app.payment.service.PaymentService;
import com.villaekinoks.app.servicableitem.ServicableItem;
import com.villaekinoks.app.servicableitem.service.ServicableItemService;
import com.villaekinoks.app.user.VillaGuestUser;
import com.villaekinoks.app.user.service.VillaGuestUserRegistrationService;
import com.villaekinoks.app.user.service.VillaGuestUserService;
import com.villaekinoks.app.utils.TimeUtils;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;
import com.villaekinoks.app.villapricing.PricingRange;
import com.villaekinoks.app.villapricing.service.PricingRangeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/villa-bookings")
@RequiredArgsConstructor
public class VillaBookingController {

  private final VillaBookingService villaBookingService;

  private final VillaGuestUserService villaGuestUserService;

  private final VillaService villaService;

  private final IyzicoPaymentProcessingService iyzicoPaymentProcessingService;

  private final PricingRangeService pricingRangeService;

  private final VillaGuestUserRegistrationService villaGuestUserRegistrationService;

  private final PaymentService paymentService;

  private final VillaBookingAdditionalServiceService villaBookingAdditionalServiceService;

  private final ServicableItemService servicableItemService;

  @GetMapping
  @VillaEkinoksAuthorized
  public GenericApiResponse<Page<VillaBookingSummaryView>> getVillaBookings(
      @RequestParam String villaid,
      @RequestParam String startdate,
      @RequestParam String enddate,
      @RequestParam String query,
      Pageable pageable) {
    List<VillaBookingSummaryView> bookings = villaBookingService
        .getAll(new String[] { villaid }, startdate, enddate, query, pageable).stream()
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

  @GetMapping("/{id}")
  public GenericApiResponse<VillaBooking> getVillaBookingById(@PathVariable String id) {
    VillaBooking booking = villaBookingService.getById(id);
    if (booking == null) {
      throw new NotFoundException();
    }
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200#40590",
        booking);
  }

  @PostMapping
  @Transactional
  public GenericApiResponse<Create_VillaBooking_WC_MLS_XAction_Response> createVillaBooking(
      @RequestBody Create_VillaBooking_WC_MLS_XAction xAction) {

    Villa villa = this.villaService.getById(xAction.getVillaid());
    if (villa == null) {
      throw new NotFoundException();
    }

    List<VillaBooking> existingBookings = this.villaBookingService.getVillaForDateClash(
        villa.getId(),
        xAction.getStartdate(),
        xAction.getEnddate());

    if (existingBookings != null && existingBookings.size() > 0) {
      throw new IllegalStateException("There is already an existing booking in the given date range.");
    }

    // Validate that all days in the booking period have pricing ranges
    LocalDate startDate = LocalDate.parse(xAction.getStartdate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
    LocalDate endDate = LocalDate.parse(xAction.getEnddate(), DateTimeFormatter.ofPattern("yyyyMMdd"));

    LocalDate currentDate = startDate;
    while (currentDate.isBefore(endDate)) {
      String dateString = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
      PricingRange pricingRange = this.pricingRangeService.getVillaPriceInDate(villa.getId(), dateString);

      if (pricingRange == null || pricingRange.getPricepernight() == null) {
        throw new BadApiRequestException(
            "No pricing available for date: " + dateString + ". Cannot create booking for dates without pricing.",
            "400#0013");
      }

      currentDate = currentDate.plusDays(1);
    }

    VillaBooking booking = new VillaBooking();
    booking.setVilla(villa);
    booking.setStartdate(xAction.getStartdate());
    booking.setEnddate(xAction.getEnddate());
    booking.setStatus(VillaBookingStatus.PENDING);
    booking.setNumberofguests(xAction.getNumberofguests());

    VillaBookingTimestamps timestamps = new VillaBookingTimestamps();
    timestamps.setCreationdate(TimeUtils.tsInstantNow().toEpochMilli());
    timestamps.setBooking(booking);

    booking.setTimestamps(timestamps);

    booking = this.villaBookingService.create(booking);

    VillaGuestUser inquiror = this.villaGuestUserService.getByLogin(xAction.getInquiror_email());
    if (inquiror == null) {
      inquiror = this.villaGuestUserRegistrationService.registerNewUser(
          xAction.getInquiror_email(),
          xAction.getInquiror_email(),
          xAction.getInquiror_firstname(),
          xAction.getInquiror_middlename(),
          xAction.getInquiror_lastname(),
          xAction.getInquiror_identitynumber(),
          xAction.getInquiror_email(),
          xAction.getInquiror_phonenumber());
    }

    booking.setInquiror(inquiror);

    booking = this.villaBookingService.create(booking);

    if (xAction.getAdditionalservices() != null) {
      for (Create_VillaBookingAdditionalService_WC_MLS_XAction service : xAction.getAdditionalservices()) {
        ServicableItem item = this.servicableItemService.getById(service.getServicableitemid());
        if (item == null) {
          throw new NotFoundException("Servicable item not found: " + service.getServicableitemid());
        }
        VillaBookingAdditionalService bookingService = new VillaBookingAdditionalService();
        bookingService.setBooking(booking);
        bookingService.setItem(item);
        bookingService.setQuantity(service.getQuantity());
        this.villaBookingAdditionalServiceService.create(bookingService);
      }

    }

    Create_VillaBooking_WC_MLS_XAction_Response response = new Create_VillaBooking_WC_MLS_XAction_Response();
    response.setId(booking.getId());

    return new GenericApiResponse<>(
        HttpStatus.CREATED.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "201#40592",
        response);
  }

  @PostMapping("/{id}/checkout")
  @Transactional
  public GenericApiResponse<Create_BookingPayment_WC_MLS_XAction_Response> createPaymentForBooking(
      @PathVariable String id,
      @RequestBody Create_BookingPayment_WC_MLS_XAction xAction,
      HttpServletRequest request) {

    VillaBooking booking = this.villaBookingService.getById(id);
    if (booking == null) {
      throw new NotFoundException();
    }

    // If pending status , we should process bookingpayment in addition to services
    if (booking.getStatus() == VillaBookingStatus.PENDING) {

      Payment payment = new Payment();

      String startdate = booking.getStartdate();
      String enddate = booking.getEnddate();

      LocalDate startDate = LocalDate.parse(startdate, DateTimeFormatter.ofPattern("yyyyMMdd"));
      LocalDate endDate = LocalDate.parse(enddate, DateTimeFormatter.ofPattern("yyyyMMdd"));

      List<PricingRange> allDates = new ArrayList<>();
      for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
        String currDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        PricingRange pricingRange = this.pricingRangeService.getVillaPriceInDate(booking.getVilla().getId(), currDate);
        allDates.add(pricingRange);
      }
      BigDecimal totalAmount = allDates.stream().reduce(BigDecimal.ZERO,
          (sum, pr) -> sum.add(new BigDecimal(pr.getPricepernight().getAmount())), BigDecimal::add);
      totalAmount = totalAmount.setScale(2);

      Set<VillaBookingAdditionalService> services = booking.getServices();
      if (services != null && services.size() > 0) {
        for (VillaBookingAdditionalService item : services) {
          ServicableItem sItem = item.getItem();
          if (sItem != null) {
            BigDecimal itemTotal = new BigDecimal(sItem.getPrice().getAmount())
                .multiply(new BigDecimal(item.getQuantity()));
            itemTotal = itemTotal.setScale(2);
            totalAmount = totalAmount
                .add(itemTotal);
          }
        }
      }

      payment.setAmount(totalAmount.toPlainString());
      payment.setCurrency(allDates.get(0).getPricepernight().getCurrency());
      payment.setCreationdate(TimeUtils.tsInstantNow().toEpochMilli());
      payment.setStatus(PaymentStatus.PENDING);

      com.iyzipay.model.Payment externalPayment = this.iyzicoPaymentProcessingService.processPayment(
          booking.getInquiror().getId(),
          booking.getId(),
          xAction.getCardholdername(),
          booking.getInquiror().getPersonalinfo().getFirstname(),
          booking.getInquiror().getPersonalinfo().getLastname(),
          booking.getInquiror().getPersonalinfo().getPhonenumber(),
          booking.getInquiror().getPersonalinfo().getEmail(),
          booking.getInquiror().getIdentitynumber(),
          getClientIpAddress(request),
          xAction.getUseraddress(),
          xAction.getUsercity(),
          xAction.getUsercountry(),
          xAction.getUserpostcode(),
          payment.getAmount(),
          payment.getCurrency().toString(),
          xAction.getCardnumber(),
          xAction.getCardexpirymonth(),
          xAction.getCardexpiryyear(),
          xAction.getCardcvc(),
          1,
          0);

      if (externalPayment.getStatus().equals("failure")) {
        throw new IllegalStateException("Payment processing failed: " + externalPayment.getErrorMessage());
      }

      payment.setExternalid(externalPayment.getPaymentId());
      payment.setStatus(PaymentStatus.COMPLETED);

      payment = this.paymentService.create(payment);
      booking.setBookingpayment(payment);
      booking.setStatus(VillaBookingStatus.CONFIRMED);
      booking = this.villaBookingService.create(booking);

      for (VillaBookingAdditionalService service : booking.getServices()) {
        service.setPayment(payment);
        this.villaBookingAdditionalServiceService.create(service);
      }

      return new GenericApiResponse<>(
          HttpStatus.CREATED.value(),
          GenericApiResponseMessages.Generic.SUCCESS,
          "201#40591",
          new Create_BookingPayment_WC_MLS_XAction_Response(
              payment.getId(),
              booking.getId(),
              externalPayment.getPaymentId()));

    } else if (booking.getStatus() == VillaBookingStatus.CONFIRMED) {
      // If confirmed status , we should process only services payment
    } else {
      throw new IllegalStateException("Cannot process payment for a completed or cancelled booking.");
    }

    return new GenericApiResponse<>(
        HttpStatus.CREATED.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "201#40593",
        new Create_BookingPayment_WC_MLS_XAction_Response(
            booking.getBookingpayment().getId(),
            booking.getId(),
            booking.getBookingpayment().getId()));
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }
}
