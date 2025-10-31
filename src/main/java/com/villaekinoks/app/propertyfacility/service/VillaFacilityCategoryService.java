package com.villaekinoks.app.propertyfacility.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.propertyfacility.VillaFacilityCategory;
import com.villaekinoks.app.propertyfacility.repository.VillaFacilityCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaFacilityCategoryService {

  private final VillaFacilityCategoryRepository villaFacilityCategoryRepository;

  public VillaFacilityCategory getById(String id) {
    return villaFacilityCategoryRepository.findById(id).orElse(null);
  }

  public VillaFacilityCategory create(VillaFacilityCategory category) {
    return villaFacilityCategoryRepository.save(category);
  }
  
  public void delete(VillaFacilityCategory category) {
    villaFacilityCategoryRepository.delete(category);
  }
}
