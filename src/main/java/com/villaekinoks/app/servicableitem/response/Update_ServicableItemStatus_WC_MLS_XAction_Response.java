package com.villaekinoks.app.servicableitem.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Update_ServicableItemStatus_WC_MLS_XAction_Response {

  private String id;
}
