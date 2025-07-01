package com.villaekinoks.app.booking;

import java.util.Set;

import org.hibernate.annotations.UuidGenerator;

import com.villaekinoks.app.villa.Villa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaBooking {

  @Id
  @UuidGenerator
  private String id;

  @ManyToOne
  @JoinColumn(name = "villa_id", nullable = false)
  private Villa villa;

  private String startdate;

  private String enddate;

  @OneToMany(mappedBy = "booking", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Set<VillaBookingGuest> guests;
}
