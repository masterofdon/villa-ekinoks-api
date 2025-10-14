package com.villaekinoks.app.villapricing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.villapricing.PricingRange;
import com.villaekinoks.app.villapricing.repository.PricingRangeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PricingRangeService {

  private final PricingRangeRepository pricingRangeRepository;

  public PricingRange getById(String id) {
    return pricingRangeRepository.findById(id).orElse(null);
  }

  public PricingRange getVillaPriceInDate(String villaId, String date) {
    return pricingRangeRepository.findVillasPriceInDate(villaId, date, date);
  }

  public PricingRange save(PricingRange pricingRange) {
    return pricingRangeRepository.save(pricingRange);
  }

  public void delete(PricingRange pricingRange) {
    pricingRangeRepository.delete(pricingRange);
  }

  public List<PricingRange> findOverlappingRanges(String villaPricingSchemaId, String startPeriod, String endPeriod) {
    return pricingRangeRepository.findOverlappingRanges(villaPricingSchemaId, startPeriod, endPeriod);
  }

  public List<PricingRange> findAllByVillaPricingSchemaOrderByStartperiod(String villaPricingSchemaId) {
    return pricingRangeRepository.findAllByVillaPricingSchemaOrderByStartperiod(villaPricingSchemaId);
  }
}
