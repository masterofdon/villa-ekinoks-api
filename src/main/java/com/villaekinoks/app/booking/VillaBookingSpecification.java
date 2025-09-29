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

  public Specification<VillaBooking> conditionalSearch(
      String[] villaIds,
      String query) {
    Specification<VillaBooking> spec = Specification.not(null);
    if (villaIds != null && villaIds.length > 0) {
      spec = spec.and(hasVillaIds(villaIds));
    }
    if (query != null) {
      spec = spec.and(query(query));
    }
    return spec;
  }
}
