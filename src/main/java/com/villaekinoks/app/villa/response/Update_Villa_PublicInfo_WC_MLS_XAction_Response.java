package com.villaekinoks.app.villa.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@AllArgsConstructor
public class Update_Villa_PublicInfo_WC_MLS_XAction_Response {

  private String id;
}
