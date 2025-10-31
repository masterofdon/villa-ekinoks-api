package com.villaekinoks.app.propertyfacility.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.propertyfacility.VillaFacility;
import com.villaekinoks.app.propertyfacility.repository.VillaFacilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaFacilityService {
  
  private final VillaFacilityRepository villaFacilityRepository;

  public VillaFacility getById(String id) {
    return villaFacilityRepository.findById(id).orElse(null);
  }

  public VillaFacility create(VillaFacility facility) {
    return villaFacilityRepository.save(facility);
  }

  public void delete(VillaFacility facility) {
    villaFacilityRepository.delete(facility);
  }
}
