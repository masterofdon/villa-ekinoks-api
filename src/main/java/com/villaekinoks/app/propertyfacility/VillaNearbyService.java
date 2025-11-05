package com.villaekinoks.app.propertyfacility;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.generic.entity.LatLng;
import com.villaekinoks.app.villa.Villa;

import jakarta.persistence.Embedded;
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
public class VillaNearbyService {

  @Id
  @UuidGenerator
  private String id;

  @ManyToOne
  @JoinColumn(name = "villa_id", nullable = false)
  @JsonIgnore
  private Villa villa;

  @Enumerated(EnumType.STRING)
  private NearbyServiceType type;

  private String name;

  private String distance;

  @Embedded
  private LatLng location;
}
