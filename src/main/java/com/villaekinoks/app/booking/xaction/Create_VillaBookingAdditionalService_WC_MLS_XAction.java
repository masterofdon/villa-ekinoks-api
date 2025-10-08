package com.villaekinoks.app.booking.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaBookingAdditionalService_WC_MLS_XAction {

  private String servicableitemid;

  private Integer quantity;
}
