package com.villaekinoks.app.payment;

import org.hibernate.annotations.UuidGenerator;

import com.villaekinoks.app.booking.VillaBooking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class PaymentRequest {

  @Id
  @UuidGenerator
  private String id;

  private Long creationdate;

  @ManyToOne
  @JoinColumn(name = "booking_id", nullable = false)
  private VillaBooking booking;

  private String userip;

  @Enumerated(EnumType.STRING)
  private PaymentRequestStatus status;

  private String email;

  private String amount;

  private String currency;

  private Integer installmentcount;

  private Integer securepayment;

  private String paymenttype;

  @Column(length = 2048)
  private String paymentUrl;

  @Column(unique = true)
  private String externalid;

  @Column(length = 2048)
  private String externaltoken;
}
