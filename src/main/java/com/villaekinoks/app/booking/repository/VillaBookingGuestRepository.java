package com.villaekinoks.app.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.booking.VillaBookingGuest;

public interface VillaBookingGuestRepository extends JpaRepository<VillaBookingGuest, String> {

  Page<VillaBookingGuest> findByBookingId(String bookingId, Pageable pageable);
}
