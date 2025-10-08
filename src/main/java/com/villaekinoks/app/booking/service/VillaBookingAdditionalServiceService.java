package com.villaekinoks.app.booking.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.booking.VillaBookingAdditionalService;
import com.villaekinoks.app.booking.repository.VillaBookingAdditionalServiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaBookingAdditionalServiceService {

  private final VillaBookingAdditionalServiceRepository villaBookingAdditionalServiceRepository;

  public VillaBookingAdditionalService getById(String id) {
    return villaBookingAdditionalServiceRepository.findById(id).orElse(null);
  }

  public VillaBookingAdditionalService create(VillaBookingAdditionalService villaBookingAdditionalService) {
    return villaBookingAdditionalServiceRepository.save(villaBookingAdditionalService);
  }

  public void delete(VillaBookingAdditionalService villaBookingAdditionalService) {
    villaBookingAdditionalServiceRepository.delete(villaBookingAdditionalService);
  }
}
