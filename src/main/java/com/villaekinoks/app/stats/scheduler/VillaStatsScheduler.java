package com.villaekinoks.app.stats.scheduler;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.villaekinoks.app.booking.VillaBooking;
import com.villaekinoks.app.booking.VillaBookingStatus;
import com.villaekinoks.app.booking.service.VillaBookingService;
import com.villaekinoks.app.stats.VillaStat;
import com.villaekinoks.app.stats.VillaStatConstants;
import com.villaekinoks.app.stats.service.VillaStatService;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.service.VillaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaStatsScheduler {

  private final VillaService villaService;

  private final VillaStatService villaStatService;

  private final VillaBookingService villaBookingService;

  @Scheduled(cron = "0 */10 * * * *")
  @Transactional
  public void generateVillaStats() {

    List<Villa> allVillas = villaService.getAll(null, null, Pageable.unpaged()).getContent();
    for (Villa villa : allVillas) {
      List<VillaStat> villaStats = this.villaStatService.getByVillaId(villa.getId());
      if (villaStats == null) {
        villaStats = List.of();
      }
      // Get all bookings for this villa
      List<VillaBooking> bookings = this.villaBookingService.getAll(new String[] { villa.getId() },
          null,
          null,
          null,
          Pageable.unpaged()).getContent();
      // 1. Generate All Bookings Stat for this villa
      VillaStat allBookingsStat = villaStats.stream()
          .filter(e -> e.getStatcode().equals(VillaStatConstants.BOOKINGS_TOTAL_STATCODE)).findFirst().orElse(null);
      if (allBookingsStat == null) {
        allBookingsStat = new VillaStat();
        allBookingsStat.setVilla(villa);
        allBookingsStat.setStatcode(VillaStatConstants.BOOKINGS_TOTAL_STATCODE);
      }
      allBookingsStat.setValue(String.valueOf(bookings.size()));
      allBookingsStat.setPrefix("");
      allBookingsStat.setSuffix("");
      allBookingsStat.setColor("blue");
      this.villaStatService.create(allBookingsStat);

      // 2. All revenue stat for this villa
      VillaStat allRevenueStat = villaStats.stream()
          .filter(e -> e.getStatcode().equals(VillaStatConstants.REVENUE_TOTAL_STATCODE)).findFirst().orElse(null);
      if (allRevenueStat == null) {
        allRevenueStat = new VillaStat();
        allRevenueStat.setVilla(villa);
        allRevenueStat.setStatcode(VillaStatConstants.REVENUE_TOTAL_STATCODE);
      }
      BigDecimal totalRevenue = bookings.stream()
          .filter(e -> {
            return e.getStatus() == VillaBookingStatus.CONFIRMED || e.getStatus() == VillaBookingStatus.COMPLETED;
          })
          .map(e -> {
            BigDecimal price = new BigDecimal(e.getBookingpayment().getAmount());
            return price;
          }).reduce(BigDecimal.ZERO, BigDecimal::add);
      totalRevenue.setScale(2);
      allRevenueStat.setValue(totalRevenue.toString());
      allRevenueStat.setPrefix("");
      allRevenueStat.setSuffix("EUR");
      allRevenueStat.setColor("green");
      this.villaStatService.create(allRevenueStat);

      // 3. Occupancy Rate Stat for this villa
      VillaStat occupancyRateStat = villaStats.stream()
          .filter(e -> e.getStatcode().equals(VillaStatConstants.VILLA_OCCUPANCY_RATE_STATCODE)).findFirst()
          .orElse(null);
      if (occupancyRateStat == null) {
        occupancyRateStat = new VillaStat();
        occupancyRateStat.setVilla(villa);
        occupancyRateStat.setStatcode(VillaStatConstants.VILLA_OCCUPANCY_RATE_STATCODE);
      }
      // Calculate occupancy rate
      // Total days in current year
      int year = java.time.LocalDate.now().getYear();
      java.time.LocalDate startOfYear = java.time.LocalDate.of(year, 1, 1);
      java.time.LocalDate endOfYear = java.time.LocalDate.of(year, 12, 31);
      long totalDaysInYear = java.time.temporal.ChronoUnit.DAYS.between(startOfYear, endOfYear) + 1;
      // Total booked days in current year
      java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
      long totalBookedDays = bookings.stream()
          .filter(e -> {
            return e.getStatus() == VillaBookingStatus.CONFIRMED || e.getStatus() == VillaBookingStatus.COMPLETED;
          })
          .mapToLong(e -> {
            java.time.LocalDate bookingStart = java.time.LocalDate.parse(e.getStartdate(), formatter);
            java.time.LocalDate bookingEnd = java.time.LocalDate.parse(e.getEnddate(), formatter);
            // Adjust booking dates to fit within the current year
            if (bookingStart.isBefore(startOfYear)) {
              bookingStart = startOfYear;
            }
            if (bookingEnd.isAfter(endOfYear)) {
              bookingEnd = endOfYear;
            }
            return java.time.temporal.ChronoUnit.DAYS.between(bookingStart, bookingEnd) + 1;
          })
          .reduce(0L, Long::sum);
      occupancyRateStat.setValue(String.valueOf((double) totalBookedDays / totalDaysInYear * 100));
      occupancyRateStat.setPrefix("");
      occupancyRateStat.setSuffix("%");
      occupancyRateStat.setColor("orange");
      this.villaStatService.create(occupancyRateStat);
    }
  }
}
