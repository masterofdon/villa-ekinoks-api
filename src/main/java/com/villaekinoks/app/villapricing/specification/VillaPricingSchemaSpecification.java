package com.villaekinoks.app.villapricing.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.villaekinoks.app.villapricing.VillaPricingSchema;

@Component
public class VillaPricingSchemaSpecification {

  public Specification<VillaPricingSchema> hasVillaIds(String[] villaIds) {
    return (root, query, cb) -> root.get("villa").get("id").in((Object[]) villaIds);
  }

  public Specification<VillaPricingSchema> hasBookingDate(String bookingDate) {
    return (root, query, cb) -> {
      return cb.and(
          cb.lessThanOrEqualTo(root.join("pricingRanges").get("startperiod"), bookingDate),
          cb.greaterThan(root.join("pricingRanges").get("endperiod"), bookingDate));
    };
  }

  public Specification<VillaPricingSchema> conditionalSearch(
      String[] villaIds,
      String bookingdate) {
    Specification<VillaPricingSchema> spec = Specification.not(null);
    if (villaIds != null && villaIds.length > 0) {
      spec = spec.and(hasVillaIds(villaIds));
    }
    if (bookingdate != null && !bookingdate.isEmpty()) {
      spec = spec.and(hasBookingDate(bookingdate));
    }
    return spec;
  }
}
