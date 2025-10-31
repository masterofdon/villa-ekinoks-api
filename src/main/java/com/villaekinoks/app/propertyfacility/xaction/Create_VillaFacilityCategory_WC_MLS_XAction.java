package com.villaekinoks.app.propertyfacility.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaFacilityCategory_WC_MLS_XAction {

  private String name;

  private Integer priority;
}
