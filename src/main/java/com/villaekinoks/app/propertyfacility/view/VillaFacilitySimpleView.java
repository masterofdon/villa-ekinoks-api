package com.villaekinoks.app.propertyfacility.view;

import com.villaekinoks.app.propertyfacility.VillaFacility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VillaFacilitySimpleView {

  private String id;

  private String name;

  private String description;

  private Integer priority;

  public VillaFacilitySimpleView(VillaFacility facility) {
    this.id = facility.getId();
    this.name = facility.getName();
    this.description = facility.getDescription();
    this.priority = facility.getPriority();
  }
}
