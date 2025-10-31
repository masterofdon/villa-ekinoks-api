package com.villaekinoks.app.villa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.villa.VillaFacilityItem;
import com.villaekinoks.app.villa.repository.VillaFacilityItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaFacilityItemService {
  
  private final VillaFacilityItemRepository villaFacilityItemRepository;

  public List<VillaFacilityItem> getAllByVillaId(String villaid){
    return villaFacilityItemRepository.findAllByVillaId(villaid);
  }

  public VillaFacilityItem create(VillaFacilityItem villaFacilityItem) {
    return villaFacilityItemRepository.save(villaFacilityItem);
  }

  public void delete(VillaFacilityItem villaFacilityItem) {
    villaFacilityItemRepository.delete(villaFacilityItem);
  }
}
