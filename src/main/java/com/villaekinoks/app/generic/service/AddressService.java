package com.villaekinoks.app.generic.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.generic.entity.Address;
import com.villaekinoks.app.generic.repository.AddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

  private final AddressRepository addressRepository;

  public Address getById(String id) {
    return addressRepository.findById(id).orElse(null);
  }

  public Address create(Address address) {
    return addressRepository.save(address);
  }

  public void delete(Address address) {
    addressRepository.delete(address);
  }
}
