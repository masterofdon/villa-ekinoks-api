package com.villaekinoks.app.villa;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.propertyfacility.VillaFacility;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaFacilityItem {

  @Id
  @UuidGenerator
  private String id;

  @ManyToOne
  @JoinColumn(name = "villa_id", nullable = false)
  @JsonIgnore
  private Villa villa;

  @ManyToOne
  @JoinColumn(name = "facility_id", nullable = false)
  private VillaFacility facility;

}
