package com.villaekinoks.app.villapricing.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.villapricing.VillaPricingSchema;
import com.villaekinoks.app.villapricing.repository.VillaPricingSchemaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaPricingSchemaService {

  private final VillaPricingSchemaRepository villaPricingSchemaRepository;

  public VillaPricingSchema getById(String id) {
    return villaPricingSchemaRepository.findById(id).orElse(null);
  }

  public VillaPricingSchema getByVillaId(String villaId) {
    return villaPricingSchemaRepository.findByVillaId(villaId);
  }

  public VillaPricingSchema create(VillaPricingSchema villaPricingSchema) {
    return villaPricingSchemaRepository.save(villaPricingSchema);
  }

  public void delete(VillaPricingSchema villaPricingSchema) {
    villaPricingSchemaRepository.delete(villaPricingSchema);
  }

}
