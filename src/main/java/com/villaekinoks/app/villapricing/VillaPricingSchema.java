package com.villaekinoks.app.villapricing;

import java.util.Set;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.generic.entity.Price;
import com.villaekinoks.app.villa.Villa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaPricingSchema {

  @Id
  @UuidGenerator
  private String id;

  @OneToOne
  @JoinColumn(name = "villa_id", nullable = false)
  @JsonIgnore
  private Villa villa;

  @Embedded
  private Price pricepernight;

  @OneToMany(mappedBy = "villapricingschema", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
  private Set<PricingRange> pricingranges;
}
