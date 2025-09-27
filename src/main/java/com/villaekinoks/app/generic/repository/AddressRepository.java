package com.villaekinoks.app.generic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.generic.entity.Address;

public interface AddressRepository extends JpaRepository<Address, String> {

}
