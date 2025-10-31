package com.villaekinoks.app.propertyfacility.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaFacility_WC_MLS_XAction {

  private String categoryid;

  private String name;

  private String description;

  private Integer priority;
}
