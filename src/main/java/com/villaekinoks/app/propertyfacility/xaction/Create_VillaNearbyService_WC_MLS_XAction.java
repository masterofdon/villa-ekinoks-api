package com.villaekinoks.app.propertyfacility.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.generic.entity.LatLng;
import com.villaekinoks.app.propertyfacility.NearbyServiceType;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaNearbyService_WC_MLS_XAction {

  private String villaid;

  private NearbyServiceType type;

  private String name;

  private String distance;

  private LatLng location;
}
