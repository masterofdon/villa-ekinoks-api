package com.villaekinoks.app.propertyfacility.view;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VillaFacilityCategoryMapView {

  private String id;

  private Integer priority;

  private List<VillaFacilitySimpleView> facilities;
}
