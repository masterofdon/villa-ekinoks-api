package com.villaekinoks.app.booking;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class VillaBookingSpecification {

  public Specification<VillaBooking> query(String _query) {
    return (root, query, cb) -> cb.or(
        cb.like(root.get("publicinfo").get("name"), "%" + _query + "%"),
        cb.like(root.get("publicinfo").get("slug"), "%" + _query + "%"),
        cb.like(root.get("publicinfo").get("description"), "%" + _query + "%"));
  }

  public Specification<VillaBooking> hasVillaIds(String[] villaIds) {
    return (root, query, cb) -> root.get("villa").get("id").in((Object[]) villaIds);
  }

  public Specification<VillaBooking> hasStartdate(String startdate) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startdate"), startdate);
  }

  public Specification<VillaBooking> hasEnddate(String enddate) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("enddate"), enddate);
  }

  /**
   * Finds bookings that clash with the given date range.
   * Two bookings clash if their date ranges overlap, meaning there's at least one
   * date
   * that exists in both booking periods.
   * 
   * Logic: Bookings clash if:
   * - existing.startdate < candidate.enddate AND candidate.startdate <
   * existing.enddate
   * 
   * @param candidateStartdate The start date of the new booking candidate
   * @param candidateEnddate   The end date of the new booking candidate
   * @return Specification that finds clashing bookings
   */
  public Specification<VillaBooking> hasDateClashForVilla(String villaId, String candidateStartdate,
      String candidateEnddate) {
    return (root, query, cb) -> cb.and(
        cb.lessThan(root.get("startdate"), candidateEnddate),
        cb.greaterThan(root.get("enddate"), candidateStartdate),
        cb.equal(root.get("villa").get("id"), villaId));
  }

  public Specification<VillaBooking> hasStatuses(VillaBookingStatus[] statuses) {
    return (root, query, cb) -> root.get("status").in((Object[]) statuses);
  }

  public Specification<VillaBooking> conditionalSearch(
      String[] villaIds,
      String startdate,
      String enddate,
      String query) {
    Specification<VillaBooking> spec = Specification.not(null);
    if (villaIds != null && villaIds.length > 0) {
      spec = spec.and(hasVillaIds(villaIds));
    }
    if (query != null) {
      spec = spec.and(query(query));
    }
    if (startdate != null) {
      spec = spec.and(hasStartdate(startdate));
    }
    if (enddate != null) {
      spec = spec.and(hasEnddate(enddate));
    }
    return spec;
  }

  /**
   * Comprehensive search that includes date clash detection.
   * Use this method when you need to find bookings and also check for date
   * conflicts.
   * 
   * @param villaIds  Array of villa IDs to filter by (optional)
   * @param query     Search query for name/slug/description (optional)
   * @param startdate Start date to check for clashes (optional)
   * @param enddate   End date to check for clashes (optional)
   * @return Specification with all the specified conditions
   */
  public Specification<VillaBooking> conditionalSearchForVillaWithClashDetection(
      String villaid,
      String startdate,
      String enddate) {

    Specification<VillaBooking> spec = Specification.not(null);
    spec = spec.and(hasDateClashForVilla(villaid, startdate, enddate));

    spec = spec.and(hasStatuses(new VillaBookingStatus[] {
        VillaBookingStatus.PENDING,
        VillaBookingStatus.CONFIRMED,
        VillaBookingStatus.COMPLETED }));

    return spec;
  }
}
