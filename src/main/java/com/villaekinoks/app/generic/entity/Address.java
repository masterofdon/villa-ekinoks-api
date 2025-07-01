package com.villaekinoks.app.generic.entity;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Address {

  @Id
  @UuidGenerator
  private String id;

  private String street;

  private String district;

  private String county;

  private String city;

  private String buildingno;

  private String postcode;

  @Embedded
  private LatLng location;

  private String placeid;
}
