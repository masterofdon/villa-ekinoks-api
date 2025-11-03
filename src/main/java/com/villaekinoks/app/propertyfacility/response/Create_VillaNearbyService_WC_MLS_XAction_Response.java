package com.villaekinoks.app.propertyfacility.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaNearbyService_WC_MLS_XAction_Response {

  private String id;
}
