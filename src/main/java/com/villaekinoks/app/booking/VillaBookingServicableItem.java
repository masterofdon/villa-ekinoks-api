package com.villaekinoks.app.booking;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.generic.entity.Price;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaBookingServicableItem {

  @Id
  @UuidGenerator
  private String id;

  @OneToOne
  @JoinColumn(name = "additional_service_id", nullable = false)
  @JsonIgnore
  private VillaBookingAdditionalService additionalservice;

  private String name;

  private String description;

  private String iconlink;

  @Embedded
  private Price price;

  private String unit;

}
