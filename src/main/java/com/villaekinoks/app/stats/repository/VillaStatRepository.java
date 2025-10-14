package com.villaekinoks.app.stats.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.stats.VillaStat;

public interface VillaStatRepository extends JpaRepository<VillaStat, String> {

  List<VillaStat> findByVillaId(String villaId);
}
