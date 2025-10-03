package com.villaekinoks.app.booking;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.payment.Payment;
import com.villaekinoks.app.servicableitem.ServicableItem;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private ServicableItem item;

  @ManyToOne
  @JoinColumn(name = "payment_id")
  private Payment payment;

  private String quantity;
}
