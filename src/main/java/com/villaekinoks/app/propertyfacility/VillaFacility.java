package com.villaekinoks.app.propertyfacility;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaFacility {

  @Id
  @UuidGenerator
  private String id;

  private String name;

  private String description;

  private Integer priority;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private VillaFacilityCategory category;
}
