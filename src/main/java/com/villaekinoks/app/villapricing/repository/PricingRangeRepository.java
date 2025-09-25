package com.villaekinoks.app.villapricing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.villaekinoks.app.villapricing.PricingRange;

public interface PricingRangeRepository extends JpaRepository<PricingRange, String> {

  @Query("""
      SELECT pr FROM PricingRange pr
      WHERE pr.villapricingschema.villa.id = :villaId
      AND pr.startperiod <= :startPeriod
      AND pr.endperiod > :endPeriod
      """)
  PricingRange findVillasPriceInDate(
      String villaId,
      String startPeriod,
      String endPeriod);

  @Query("""
      SELECT pr FROM PricingRange pr
      WHERE pr.villapricingschema.id = :villaPricingSchemaId
      AND pr.startperiod < :endPeriod
      AND pr.endperiod > :startPeriod
      """)
  List<PricingRange> findOverlappingRanges(
      String villaPricingSchemaId,
      String startPeriod,
      String endPeriod);
}
