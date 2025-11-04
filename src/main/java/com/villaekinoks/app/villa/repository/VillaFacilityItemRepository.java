package com.villaekinoks.app.villa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.villa.VillaFacilityItem;

public interface VillaFacilityItemRepository extends JpaRepository<VillaFacilityItem, String> {

  List<VillaFacilityItem> findAllByVillaId(String villaid);
  
  Optional<VillaFacilityItem> findByVillaIdAndFacilityId(String villaId, String facilityId);
}
