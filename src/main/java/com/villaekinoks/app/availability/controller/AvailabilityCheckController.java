package com.villaekinoks.app.availability.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.availability.response.Get_AvailabilityCheck_XAction_Response;
import com.villaekinoks.app.availability.view.DateAvailability;
import com.villaekinoks.app.booking.VillaBooking;
import com.villaekinoks.app.booking.service.VillaBookingService;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.generic.entity.Price;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;
import com.villaekinoks.app.villapricing.PricingRange;
import com.villaekinoks.app.villapricing.service.PricingRangeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/availability-check-controller")
@RequiredArgsConstructor
public class AvailabilityCheckController {

  private final VillaBookingService villaBookingService;

  private final VillaService villaService;

  private final PricingRangeService pricingRangeService;

  @GetMapping("/check-villa-availability")
  public GenericApiResponse<Get_AvailabilityCheck_XAction_Response> checkVillaAvailability(
      @RequestParam String villaid, @RequestParam String startdate, @RequestParam String enddate) {
    Villa villa = this.villaService.getById(villaid);
    if (villa == null) {
      throw new NotFoundException();
    }

    LocalDate checkoutStartDate = LocalDate.parse(startdate, DateTimeFormatter.ofPattern("yyyyMMdd"));
    LocalDate checkoutEndDate = LocalDate.parse(enddate, DateTimeFormatter.ofPattern("yyyyMMdd"));

    int daysBetween = (int) (checkoutEndDate.toEpochDay() - checkoutStartDate.toEpochDay());

    List<VillaBooking> bookings = this.villaBookingService.getVillaForDateClash(villaid, startdate, enddate);

    // Check if pricing is available for all dates in the requested range
    boolean hasPricingForAllDates = checkPricingAvailability(villaid, checkoutStartDate, checkoutEndDate);

    // Build date availabilities for the requested range
    List<DateAvailability> dateAvailabilities = buildDateAvailabilities(villaid, checkoutStartDate, checkoutEndDate, bookings);

    if (bookings != null && bookings.size() > 0) {
      // There are bookings in the date range, so not all dates are available
      // Find alternative date ranges with the same length

      Get_AvailabilityCheck_XAction_Response response = new Get_AvailabilityCheck_XAction_Response();
      response.setAllavailable(false);
      response.setDateavailabilities(dateAvailabilities);

      // Try to find alternative dates (considering both booking conflicts and pricing
      // availability)
      String[] alternativeDates = findAlternativeDateRange(villaid, checkoutStartDate, checkoutEndDate, daysBetween,
          bookings);

      if (alternativeDates != null) {
        response.setNextavailablestartdate(alternativeDates[0]);
        response.setNextavailableenddate(alternativeDates[1]);
      }

      return new GenericApiResponse<>(
          HttpStatus.OK.value(),
          GenericApiResponseMessages.Generic.SUCCESS,
          "200#0001",
          response);
    } else if (!hasPricingForAllDates) {
      // No booking conflicts but pricing is not available for all dates
      Get_AvailabilityCheck_XAction_Response response = new Get_AvailabilityCheck_XAction_Response();
      response.setAllavailable(false);
      response.setDateavailabilities(dateAvailabilities);

      // Try to find alternative dates with both available booking slots and pricing
      String[] alternativeDates = findAlternativeDateRangeWithPricing(villaid, checkoutStartDate, checkoutEndDate,
          daysBetween);

      if (alternativeDates != null) {
        response.setNextavailablestartdate(alternativeDates[0]);
        response.setNextavailableenddate(alternativeDates[1]);
      }

      return new GenericApiResponse<>(
          HttpStatus.OK.value(),
          GenericApiResponseMessages.Generic.SUCCESS,
          "200#0002", // Different response code for pricing issues
          response);
    } else {
      // No bookings in the date range and pricing is available for all dates
      Get_AvailabilityCheck_XAction_Response response = new Get_AvailabilityCheck_XAction_Response();
      response.setAllavailable(true);
      response.setDateavailabilities(dateAvailabilities);
      return new GenericApiResponse<>(
          HttpStatus.OK.value(),
          GenericApiResponseMessages.Generic.SUCCESS,
          "200#0000",
          response);
    }
  }

  /**
   * Finds alternative date ranges when the requested dates are not available.
   * 
   * @param villaId             The villa ID to check
   * @param requestedStart      The originally requested start date
   * @param requestedEnd        The originally requested end date
   * @param daysBetween         The length of the stay in days
   * @param conflictingBookings The bookings that conflict with the requested
   *                            dates
   * @return Array with [startDate, endDate] if found, null if no alternative
   *         found
   */
  private String[] findAlternativeDateRange(String villaId, LocalDate requestedStart, LocalDate requestedEnd,
      int daysBetween, List<VillaBooking> conflictingBookings) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    // Try to find dates before the requested range
    String[] beforeDates = findDateRangeBefore(villaId, requestedStart, daysBetween, conflictingBookings, formatter);
    if (beforeDates != null) {
      return beforeDates;
    }

    // Try to find dates after the requested range
    String[] afterDates = findDateRangeAfter(villaId, requestedEnd, daysBetween, conflictingBookings, formatter);
    if (afterDates != null) {
      return afterDates;
    }

    return null; // No alternative found
  }

  /**
   * Finds available dates before the requested range.
   * Ensures the new range ends when an existing booking starts.
   */
  private String[] findDateRangeBefore(String villaId, LocalDate requestedStart, int daysBetween,
      List<VillaBooking> conflictingBookings, DateTimeFormatter formatter) {

    // Look for daysBetween days before the requested start date
    LocalDate searchEnd = requestedStart;

    // For each day in the search range, try to find a valid period
    for (int offset = 0; offset <= daysBetween; offset++) {
      LocalDate candidateEnd = searchEnd.minusDays(offset);
      LocalDate candidateStart = candidateEnd.minusDays(daysBetween);

      // Check if this candidate range clashes with any bookings
      String candidateStartStr = candidateStart.format(formatter);
      String candidateEndStr = candidateEnd.format(formatter);

      List<VillaBooking> clashingBookings = villaBookingService.getVillaForDateClash(
          villaId, candidateStartStr, candidateEndStr);

      if ((clashingBookings == null || clashingBookings.isEmpty()) &&
          checkPricingAvailability(villaId, candidateStart, candidateEnd)) {
        // Found a valid range with no booking conflicts and pricing available
        return new String[] { candidateStartStr, candidateEndStr };
      }

      // Try to align with existing booking end dates
      for (VillaBooking booking : conflictingBookings) {
        LocalDate bookingStart = LocalDate.parse(booking.getStartdate(), formatter);
        LocalDate alignedEnd = bookingStart;
        LocalDate alignedStart = alignedEnd.minusDays(daysBetween);

        if (alignedStart.isAfter(requestedStart.minusDays(daysBetween))) {
          String alignedStartStr = alignedStart.format(formatter);
          String alignedEndStr = alignedEnd.format(formatter);

          List<VillaBooking> alignedClashes = villaBookingService.getVillaForDateClash(
              villaId, alignedStartStr, alignedEndStr);

          if (alignedClashes == null || alignedClashes.isEmpty()) {
            return new String[] { alignedStartStr, alignedEndStr };
          }
        }
      }
    }

    return null; // No suitable range found before
  }

  /**
   * Finds available dates after the requested range.
   * Ensures the new range starts when an existing booking ends.
   */
  private String[] findDateRangeAfter(String villaId, LocalDate requestedEnd, int daysBetween,
      List<VillaBooking> conflictingBookings, DateTimeFormatter formatter) {

    // Look for daysBetween days after the requested end date
    LocalDate searchStart = requestedEnd;

    // For each day in the search range, try to find a valid period
    for (int offset = 0; offset <= daysBetween; offset++) {
      LocalDate candidateStart = searchStart.plusDays(offset);
      LocalDate candidateEnd = candidateStart.plusDays(daysBetween);

      // Check if this candidate range clashes with any bookings
      String candidateStartStr = candidateStart.format(formatter);
      String candidateEndStr = candidateEnd.format(formatter);

      List<VillaBooking> clashingBookings = villaBookingService.getVillaForDateClash(
          villaId, candidateStartStr, candidateEndStr);

      if ((clashingBookings == null || clashingBookings.isEmpty()) &&
          checkPricingAvailability(villaId, candidateStart, candidateEnd)) {
        // Found a valid range with no booking conflicts and pricing available
        return new String[] { candidateStartStr, candidateEndStr };
      }

      // Try to align with existing booking end dates
      for (VillaBooking booking : conflictingBookings) {
        LocalDate bookingEnd = LocalDate.parse(booking.getEnddate(), formatter);
        LocalDate alignedStart = bookingEnd;
        LocalDate alignedEnd = alignedStart.plusDays(daysBetween);

        if (alignedEnd.isBefore(requestedEnd.plusDays(daysBetween))) {
          String alignedStartStr = alignedStart.format(formatter);
          String alignedEndStr = alignedEnd.format(formatter);

          List<VillaBooking> alignedClashes = villaBookingService.getVillaForDateClash(
              villaId, alignedStartStr, alignedEndStr);

          if (alignedClashes == null || alignedClashes.isEmpty()) {
            return new String[] { alignedStartStr, alignedEndStr };
          }
        }
      }
    }

    return null; // No suitable range found after
  }

  /**
   * Checks if pricing is available for all dates in the given range.
   * 
   * @param villaId   The villa ID to check pricing for
   * @param startDate The start date of the range
   * @param endDate   The end date of the range
   * @return true if pricing is available for all dates, false otherwise
   */
  private boolean checkPricingAvailability(String villaId, LocalDate startDate, LocalDate endDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    LocalDate currentDate = startDate;
    while (currentDate.isBefore(endDate)) {
      String dateString = currentDate.format(formatter);
      PricingRange pricing = pricingRangeService.getVillaPriceInDate(villaId, dateString);

      if (pricing == null) {
        return false; // No pricing found for this date
      }

      currentDate = currentDate.plusDays(1);
    }

    return true; // All dates have pricing
  }

  /**
   * Builds a list of DateAvailability objects for the given date range.
   * Each date includes availability status and pricing information.
   * 
   * @param villaId The villa ID to check
   * @param startDate The start date of the range
   * @param endDate The end date of the range  
   * @param bookings List of existing bookings that may conflict
   * @return List of DateAvailability objects with availability and pricing info
   */
  private List<DateAvailability> buildDateAvailabilities(String villaId, LocalDate startDate, LocalDate endDate, List<VillaBooking> bookings) {
    List<DateAvailability> dateAvailabilities = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    LocalDate currentDate = startDate;
    while (currentDate.isBefore(endDate)) {
      DateAvailability dateAvailability = new DateAvailability();
      String dateString = currentDate.format(formatter);
      
      // Set the date
      dateAvailability.setDate(dateString);
      
      // Check if this date is available (no booking conflicts)
      boolean isAvailable = true;
      if (bookings != null) {
        for (VillaBooking booking : bookings) {
          LocalDate bookingStart = LocalDate.parse(booking.getStartdate(), formatter);
          LocalDate bookingEnd = LocalDate.parse(booking.getEnddate(), formatter);
          
          // Check if current date falls within this booking
          if (!currentDate.isBefore(bookingStart) && currentDate.isBefore(bookingEnd)) {
            isAvailable = false;
            break;
          }
        }
      }
      dateAvailability.setAvailable(isAvailable);
      
      // Get pricing for this date
      PricingRange pricingRange = pricingRangeService.getVillaPriceInDate(villaId, dateString);
      if (pricingRange != null && pricingRange.getPricepernight() != null) {
        // Create a copy of the price to avoid modifying the original
        Price price = new Price();
        price.setAmount(pricingRange.getPricepernight().getAmount());
        price.setCurrency(pricingRange.getPricepernight().getCurrency());
        dateAvailability.setPrice(price);
      }
      
      dateAvailabilities.add(dateAvailability);
      currentDate = currentDate.plusDays(1);
    }
    
    return dateAvailabilities;
  }

  /**
   * Finds alternative date ranges that have both available booking slots and
   * pricing.
   * 
   * @param villaId        The villa ID to check
   * @param requestedStart The originally requested start date
   * @param requestedEnd   The originally requested end date
   * @param daysBetween    The length of the stay in days
   * @return Array with [startDate, endDate] if found, null if no alternative
   *         found
   */
  private String[] findAlternativeDateRangeWithPricing(String villaId, LocalDate requestedStart, LocalDate requestedEnd,
      int daysBetween) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    // Try to find dates before the requested range
    String[] beforeDates = findDateRangeBeforeWithPricing(villaId, requestedStart, daysBetween, formatter);
    if (beforeDates != null) {
      return beforeDates;
    }

    // Try to find dates after the requested range
    String[] afterDates = findDateRangeAfterWithPricing(villaId, requestedEnd, daysBetween, formatter);
    if (afterDates != null) {
      return afterDates;
    }

    return null; // No alternative found
  }

  /**
   * Finds available dates before the requested range that have both no booking
   * conflicts and pricing.
   */
  private String[] findDateRangeBeforeWithPricing(String villaId, LocalDate requestedStart, int daysBetween,
      DateTimeFormatter formatter) {
    // Look for daysBetween days before the requested start date
    LocalDate searchEnd = requestedStart;

    // For each day in the search range, try to find a valid period
    for (int offset = 0; offset <= daysBetween; offset++) {
      LocalDate candidateEnd = searchEnd.minusDays(offset);
      LocalDate candidateStart = candidateEnd.minusDays(daysBetween);

      // Check if this candidate range clashes with any bookings
      String candidateStartStr = candidateStart.format(formatter);
      String candidateEndStr = candidateEnd.format(formatter);

      List<VillaBooking> clashingBookings = villaBookingService.getVillaForDateClash(
          villaId, candidateStartStr, candidateEndStr);

      // Check if there are no booking conflicts AND pricing is available for all
      // dates
      if ((clashingBookings == null || clashingBookings.isEmpty()) &&
          checkPricingAvailability(villaId, candidateStart, candidateEnd)) {
        return new String[] { candidateStartStr, candidateEndStr };
      }
    }

    return null; // No suitable range found before
  }

  /**
   * Finds available dates after the requested range that have both no booking
   * conflicts and pricing.
   */
  private String[] findDateRangeAfterWithPricing(String villaId, LocalDate requestedEnd, int daysBetween,
      DateTimeFormatter formatter) {
    // Look for daysBetween days after the requested end date
    LocalDate searchStart = requestedEnd;

    // For each day in the search range, try to find a valid period
    for (int offset = 0; offset <= daysBetween; offset++) {
      LocalDate candidateStart = searchStart.plusDays(offset);
      LocalDate candidateEnd = candidateStart.plusDays(daysBetween);

      // Check if this candidate range clashes with any bookings
      String candidateStartStr = candidateStart.format(formatter);
      String candidateEndStr = candidateEnd.format(formatter);

      List<VillaBooking> clashingBookings = villaBookingService.getVillaForDateClash(
          villaId, candidateStartStr, candidateEndStr);

      // Check if there are no booking conflicts AND pricing is available for all
      // dates
      if ((clashingBookings == null || clashingBookings.isEmpty()) &&
          checkPricingAvailability(villaId, candidateStart, candidateEnd)) {
        return new String[] { candidateStartStr, candidateEndStr };
      }
    }

    return null; // No suitable range found after
  }
}
