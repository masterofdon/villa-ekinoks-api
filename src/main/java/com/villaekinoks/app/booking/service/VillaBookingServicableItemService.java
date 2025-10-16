package com.villaekinoks.app.booking.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.booking.VillaBookingServicableItem;
import com.villaekinoks.app.booking.repository.VillaBookingServicableItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaBookingServicableItemService {

  private final VillaBookingServicableItemRepository villaBookingServicableItemRepository;

  public VillaBookingServicableItem getById(String id) {
    return this.villaBookingServicableItemRepository.findById(id).orElse(null);
  }

  public VillaBookingServicableItem create(VillaBookingServicableItem item) {
    return this.villaBookingServicableItemRepository.save(item);
  }

  public void delete(VillaBookingServicableItem item) {
    this.villaBookingServicableItemRepository.delete(item);
  }
}
