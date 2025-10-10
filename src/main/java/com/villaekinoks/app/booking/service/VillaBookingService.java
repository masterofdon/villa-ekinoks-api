package com.villaekinoks.app.booking.service;

import java.util.List;

import org.springframework.data.domain.Page;
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

  public Page<VillaBooking> getByVillaId(String villaid, Pageable pageable) {
    return villaBookingRepository
        .findAll(villaBookingSpecification.conditionalSearch(new String[] { villaid }, null, null, null), pageable);
  }

  public Page<VillaBooking> getAll(String[] villaids, String startdate, String enddate, String query,
      Pageable pageable) {
    return villaBookingRepository
        .findAll(villaBookingSpecification.conditionalSearch(villaids, startdate, enddate, query), pageable);
  }

  public VillaBooking create(VillaBooking villaBooking) {
    return villaBookingRepository.save(villaBooking);
  }

  public List<VillaBooking> getVillaForDateClash(String villaid, String startdate, String enddate) {
    return villaBookingRepository.findAll(villaBookingSpecification.hasDateClashForVilla(villaid, startdate, enddate));
  }
}
