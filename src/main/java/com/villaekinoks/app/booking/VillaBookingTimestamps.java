package com.villaekinoks.app.booking;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaBookingTimestamps {
  
  @Id
  @UuidGenerator
  private String id;
}
