package com.villaekinoks.app.propertyfacility.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.propertyfacility.VillaNearbyService;

public interface VillaNearbyServiceRepository extends JpaRepository<VillaNearbyService, String> {

  List<VillaNearbyService> findByVillaId(String villaId);

}
