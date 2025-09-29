package com.villaekinoks.app.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.villaekinoks.app.booking.VillaBooking;

public interface VillaBookingRepository
    extends JpaRepository<VillaBooking, String>, JpaSpecificationExecutor<VillaBooking> {

}
