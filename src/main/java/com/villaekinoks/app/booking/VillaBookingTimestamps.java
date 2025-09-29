package com.villaekinoks.app.booking;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaBookingTimestamps {

  @Id
  @UuidGenerator
  private String id;

  private Long creationdate;

  private Long lastupdate;

  @OneToOne
  @JoinColumn(name = "booking_id", nullable = false)
  @JsonIgnore
  private VillaBooking booking;
}
