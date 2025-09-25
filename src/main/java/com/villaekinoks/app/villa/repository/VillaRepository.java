package com.villaekinoks.app.villa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.villaekinoks.app.villa.Villa;

public interface VillaRepository extends JpaRepository<Villa, String>, JpaSpecificationExecutor<Villa> {

  Optional<Villa> findByPublicinfoSlug(String slug);

}
