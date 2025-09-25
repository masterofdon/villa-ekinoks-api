package com.villaekinoks.app.villa.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.villa.VillaOperator;
import com.villaekinoks.app.villa.repository.VillaOperatorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaOperatorService {

  private final VillaOperatorRepository villaOperatorRepository;

  public VillaOperator getById(String id) {
    return villaOperatorRepository.findById(id).orElse(null);
  }

  public Set<VillaOperator> getAllByVillaId(String villaId) {
    return villaOperatorRepository.findAllByVillaId(villaId);
  }

  public VillaOperator create(VillaOperator villaOperator) {
    return villaOperatorRepository.save(villaOperator);
  }

  public void delete(VillaOperator villaOperator) {
    villaOperatorRepository.delete(villaOperator);
  }
}