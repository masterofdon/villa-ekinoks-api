package com.villaekinoks.app.discount.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.discount.DiscountCode;
import com.villaekinoks.app.discount.repository.DiscountCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscountCodeService {

  private final DiscountCodeRepository discountCodeRepository;

  public DiscountCode getById(String id) {
    return discountCodeRepository.findById(id).orElse(null);
  }

  public DiscountCode getByCodeAndVillaId(String code, String villaId) {
    return this.discountCodeRepository.findByCodeAndVillaId(code, villaId);
  }

  public List<DiscountCode> getAll(String villaId) {
    return this.discountCodeRepository.findByVillaId(villaId);
  }

  public DiscountCode create(DiscountCode discountCode) {
    return this.discountCodeRepository.save(discountCode);
  }

  public void delete(DiscountCode discountCode) {
    this.discountCodeRepository.delete(discountCode);
  }
}
