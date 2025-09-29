package com.villaekinoks.app.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.villaekinoks.app.booking.VillaBookingGuest;
import com.villaekinoks.app.booking.repository.VillaBookingGuestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaBookingGuestService {

  private final VillaBookingGuestRepository villaBookingGuestRepository;

  public VillaBookingGuest getById(String id) {
    return villaBookingGuestRepository.findById(id).orElse(null);
  }

  public Page<VillaBookingGuest> getByBookingId(String bookingId, Pageable pageable) {
    return villaBookingGuestRepository.findByBookingId(bookingId, pageable);
  }

  public VillaBookingGuest create(VillaBookingGuest guest) {
    return villaBookingGuestRepository.save(guest);
  }

  public void delete(VillaBookingGuest guest) {
    villaBookingGuestRepository.delete(guest);
  }
}
