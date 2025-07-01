package com.villaekinoks.app.booking;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaBookingGuest {

  @Id
  @UuidGenerator
  private String id;

  @OneToOne(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private VillaBookingGuestPersonalInfo personalinfo;

  @ManyToOne
  @JoinColumn(name = "booking_id", nullable = false)
  @JsonIgnore
  private VillaBooking booking;
}
