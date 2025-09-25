package com.villaekinoks.app.villapricing;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.generic.entity.Price;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class PricingRange {

  @Id
  @UuidGenerator
  private String id;

  private String startperiod;

  private String endperiod;

  @Embedded
  private Price pricepernight;

  @ManyToOne
  @JoinColumn(name = "villapricingschema_id", nullable = false)
  @JsonIgnore
  private VillaPricingSchema villapricingschema;
}
