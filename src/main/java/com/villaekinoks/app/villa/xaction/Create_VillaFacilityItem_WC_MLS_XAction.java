package com.villaekinoks.app.villa.xaction;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaFacilityItem_WC_MLS_XAction {

  private List<String> villafacilityid;
}
