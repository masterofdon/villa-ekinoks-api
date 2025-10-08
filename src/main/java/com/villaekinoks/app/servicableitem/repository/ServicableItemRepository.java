package com.villaekinoks.app.servicableitem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.servicableitem.ServicableItem;

public interface ServicableItemRepository extends JpaRepository<ServicableItem, String> {

  Page<ServicableItem> findAllByVillaId(String villaid, Pageable pageable);

}
