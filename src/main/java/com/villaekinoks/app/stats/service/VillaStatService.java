package com.villaekinoks.app.stats.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.stats.VillaStat;
import com.villaekinoks.app.stats.repository.VillaStatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaStatService {

  private final VillaStatRepository villaStatRepository;

  public VillaStat getById(String id) {
    return villaStatRepository.findById(id).orElse(null);
  }

  public List<VillaStat> getByVillaId(String villaId) {
    return villaStatRepository.findByVillaId(villaId);
  }

  public VillaStat create(VillaStat villaStat) {
    return villaStatRepository.save(villaStat);
  }

  public void delete(VillaStat villaStat) {
    villaStatRepository.delete(villaStat);
  }
}
