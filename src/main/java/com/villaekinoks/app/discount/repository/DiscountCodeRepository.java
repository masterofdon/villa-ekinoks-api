package com.villaekinoks.app.discount.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.discount.DiscountCode;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, String> {

  DiscountCode findByCodeAndVillaId(String code, String villaId);

  List<DiscountCode> findByVillaId(String villaId);
}
