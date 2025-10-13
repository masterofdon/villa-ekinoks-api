package com.villaekinoks.app.villapricing.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.villaekinoks.app.booking.VillaBookingStatus;
import com.villaekinoks.app.user.VillaGuestUser;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class SimpleVillaBooking {

  private String id;

  private String startdate;

  private String enddate;

  private VillaBookingStatus status;

  @JsonIncludeProperties({ "id", "personalinfo" })
  private VillaGuestUser inquiror;
}
