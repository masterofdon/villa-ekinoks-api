package com.villaekinoks.app.villa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.repository.VillaRepository;
import com.villaekinoks.app.villa.specification.VillaSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaService {

  private final VillaRepository villaRepository;

  private final VillaSpecification villaSpecification;

  public Villa getById(String id) {
    return villaRepository.findById(id).orElse(null);
  }

  public Villa getBySlug(String slug) {
    return villaRepository.findByPublicinfoSlug(slug).orElse(null);
  }

  public Page<Villa> getAll(String query, String[] ids, Pageable pageable) {
    return villaRepository.findAll(villaSpecification.conditionalSearch(query, ids), pageable);
  }

  public Villa create(Villa villa) {
    return villaRepository.save(villa);
  }

  public void delete(Villa villa){
    villaRepository.delete(villa);
  }
}
