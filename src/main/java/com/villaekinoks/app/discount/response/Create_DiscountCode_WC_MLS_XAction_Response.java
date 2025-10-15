package com.villaekinoks.app.discount.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_DiscountCode_WC_MLS_XAction_Response {

  private String id;
}
