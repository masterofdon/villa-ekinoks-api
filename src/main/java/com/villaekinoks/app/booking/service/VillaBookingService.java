package com.villaekinoks.app.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.villaekinoks.app.booking.VillaBooking;
import com.villaekinoks.app.booking.VillaBookingSpecification;
import com.villaekinoks.app.booking.repository.VillaBookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaBookingService {

  private final VillaBookingRepository villaBookingRepository;

  private final VillaBookingSpecification villaBookingSpecification;

  public VillaBooking getById(String id) {
    return villaBookingRepository.findById(id).orElse(null);
  }

  public Page<VillaBooking> getAll(String[] villaids, Pageable pageable) {
    return villaBookingRepository.findAll(villaBookingSpecification.conditionalSearch(villaids, null), pageable);
  }

  public VillaBooking create(VillaBooking villaBooking) {
    return villaBookingRepository.save(villaBooking);
  }
}
