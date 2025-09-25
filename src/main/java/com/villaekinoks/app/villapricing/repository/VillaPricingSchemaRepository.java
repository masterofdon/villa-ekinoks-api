package com.villaekinoks.app.villapricing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.villaekinoks.app.villapricing.VillaPricingSchema;

public interface VillaPricingSchemaRepository
    extends JpaRepository<VillaPricingSchema, String>, JpaSpecificationExecutor<VillaPricingSchema> {

  VillaPricingSchema findByVillaId(String villaId);
}
