package com.villaekinoks.app.villa.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.villa.VillaOperator;

public interface VillaOperatorRepository extends JpaRepository<VillaOperator, String> {

  Set<VillaOperator> findAllByVillaId(String villaId);

}
