package com.villaekinoks.app.villa.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Update_Villa_PublicInfo_WC_MLS_XAction {

  private String name;

  private String promotext;

  private String description;
}
