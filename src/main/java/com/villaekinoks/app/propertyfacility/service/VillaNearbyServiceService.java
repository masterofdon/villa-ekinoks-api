package com.villaekinoks.app.propertyfacility.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.propertyfacility.VillaNearbyService;
import com.villaekinoks.app.propertyfacility.repository.VillaNearbyServiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaNearbyServiceService {

  private final VillaNearbyServiceRepository villaNearbyServiceRepository;

  public VillaNearbyService getById(String id) {
    return villaNearbyServiceRepository.findById(id).orElse(null);
  }

  public List<VillaNearbyService> getByVillaId(String villaId) {
    return villaNearbyServiceRepository.findByVillaId(villaId);
  }

  public VillaNearbyService create(VillaNearbyService villaNearbyService) {
    return villaNearbyServiceRepository.save(villaNearbyService);
  }

  public void delete(VillaNearbyService service) {
    villaNearbyServiceRepository.delete(service);
  }
}
