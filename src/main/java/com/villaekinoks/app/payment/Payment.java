package com.villaekinoks.app.payment;

import org.hibernate.annotations.UuidGenerator;

import com.villaekinoks.app.generic.entity.Currency;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Payment {

  @Id
  @UuidGenerator
  private String id;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  private String externalid;

  private String amount;

  @Enumerated(EnumType.STRING)
  private Currency currency;

  private Long creationdate;

}
