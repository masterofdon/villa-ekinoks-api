package com.villaekinoks.app.servicableitem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.servicableitem.ServicableItem;

public interface ServicableItemRepository extends JpaRepository<ServicableItem, String> {

}
