package com.villaekinoks.app.booking;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.payment.Payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaBookingAdditionalService {

  @Id
  @UuidGenerator
  private String id;

  @ManyToOne
  @JoinColumn(name = "booking_id", nullable = false)
  @JsonIgnore
  private VillaBooking booking;

  @OneToOne(mappedBy = "additionalservice", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
  private VillaBookingServicableItem item;

  @ManyToOne
  @JoinColumn(name = "payment_id")
  private Payment payment;

  private Integer quantity;
}
