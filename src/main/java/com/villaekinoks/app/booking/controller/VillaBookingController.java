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
import com.villaekinoks.app.booking.VillaBookingServicableItem;
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
import com.villaekinoks.app.discount.service.DiscountCodeUtilService;
import com.villaekinoks.app.exception.BadApiRequestException;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.mail.service.AsyncEmailService;
import com.villaekinoks.app.payment.Payment;
import com.villaekinoks.app.payment.PaymentStatus;
import com.villaekinoks.app.payment.service.IyzicoPaymentProcessingService;
import com.villaekinoks.app.payment.service.PaymentService;
import com.villaekinoks.app.servicableitem.ServicableItem;
import com.villaekinoks.app.servicableitem.service.ServicableItemService;
import com.villaekinoks.app.user.LockStatus;
import com.villaekinoks.app.user.OperationStatus;
import com.villaekinoks.app.user.ServiceStatus;
import com.villaekinoks.app.user.VerificationStatus;
import com.villaekinoks.app.user.VillaGuestUser;
import com.villaekinoks.app.user.service.VillaGuestUserService;
import com.villaekinoks.app.utils.IPUtils;
import com.villaekinoks.app.utils.TimeUtils;
import com.villaekinoks.app.verification.VerificationPair;
import com.villaekinoks.app.verification.VerificationPairStatus;
import com.villaekinoks.app.verification.service.VerificationPairService;
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

  private final PaymentService paymentService;

  private final VillaBookingAdditionalServiceService villaBookingAdditionalServiceService;

  private final ServicableItemService servicableItemService;

  private final AsyncEmailService asyncEmailService;

  private final DiscountCodeUtilService discountCodeUtilService;

  private final VerificationPairService verificationPairService;

  @GetMapping
  @VillaEkinoksAuthorized
  public GenericApiResponse<Page<VillaBookingSummaryView>> getVillaBookings(
      @RequestParam String villaid,
      @RequestParam(required = false) String startdate,
      @RequestParam(required = false) String enddate,
      @RequestParam(required = false) String query,
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

    VerificationPair vPair = this.verificationPairService.getById(xAction.getVerificationid());
    if (vPair == null) {
      throw new BadApiRequestException("Invalid verification ID.", "400#0020");
    }
    if (!vPair.getVerificationcode().equals(xAction.getVerificationcode())) {
      throw new BadApiRequestException("Invalid verification code.", "400#0021");
    }

    if (vPair.getExpirationdate() < TimeUtils.tsInstantNow().toEpochMilli()) {
      throw new BadApiRequestException("Verification code has expired.", "400#0022");
    }

    VillaGuestUser inquiror = this.villaGuestUserService.getById(vPair.getId());
    if (inquiror == null) {
      throw new NotFoundException("Inquiror user not found.");
    }

    if (inquiror.getStatusset().getLockstatus() == LockStatus.LOCKED) {
      inquiror.getStatusset().setLockstatus(LockStatus.UNLOCKED);
    }
    if (inquiror.getStatusset().getOperationstatus() == OperationStatus.DISABLED) {
      inquiror.getStatusset().setOperationstatus(OperationStatus.ENABLED);
    }

    if (inquiror.getStatusset().getServicestatus() == ServiceStatus.INACTIVE) {
      inquiror.getStatusset().setServicestatus(ServiceStatus.ACTIVE);
    }

    if (inquiror.getStatusset().getVerificationstatus() == VerificationStatus.PENDING) {
      inquiror.getStatusset().setVerificationstatus(VerificationStatus.VERIFIED);
      inquiror.getTimestamps().setVerificationdate(TimeUtils.tsInstantNow().toEpochMilli());
    }
    vPair.setStatus(VerificationPairStatus.VERIFIED);
    vPair = this.verificationPairService.create(vPair);
    inquiror = this.villaGuestUserService.create(inquiror);

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

        VillaBookingServicableItem bookingItem = new VillaBookingServicableItem();
        bookingItem.setName(item.getName());
        bookingItem.setDescription(item.getDescription());
        bookingItem.setPrice(item.getPrice());
        bookingItem.setIconlink(item.getIconlink());
        bookingItem.setUnit(item.getUnit());

        bookingItem.setAdditionalservice(bookingService);

        bookingService.setItem(bookingItem);
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
          VillaBookingServicableItem sItem = item.getItem();
          if (sItem != null) {
            BigDecimal itemTotal = new BigDecimal(sItem.getPrice().getAmount())
                .multiply(new BigDecimal(item.getQuantity()));
            itemTotal = itemTotal.setScale(2);
            totalAmount = totalAmount
                .add(itemTotal);
          }
        }
      }

      // Apply discount if provided
      if (xAction.getDiscountcode() != null && !xAction.getDiscountcode().trim().isEmpty()) {
        totalAmount = this.discountCodeUtilService.applyDiscountCode(xAction.getDiscountcode(),
            booking.getVilla().getId(), totalAmount);
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
          IPUtils.getClientIpAddress(request),
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

      // Send confirmation email to guest
      try {
        this.asyncEmailService.sendBookingPaymentSuccessEmailAsync(booking, "en");
      } catch (Exception e) {
        // Log error but don't fail the booking process
        // Email sending should not affect the booking transaction
      }

      // Send notification email to villa owner
      try {
        if (booking.getVilla().getOwner() != null &&
            booking.getVilla().getOwner().getPersonalinfo() != null &&
            booking.getVilla().getOwner().getPersonalinfo().getEmail() != null) {
          this.asyncEmailService.sendBookingConfirmedOwnerEmailAsync(
              booking,
              booking.getVilla().getOwner().getPersonalinfo().getEmail(),
              "en");
        }
      } catch (Exception e) {
        // Log error but don't fail the booking process
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

}
