package com.villaekinoks.app.booking.xaction;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaBooking_WC_MLS_XAction {

  private String verificationcode;

  private String verificationid;

  private String villaid;

  private Integer numberofguests = -1;

  private String startdate;

  private String enddate;

  private List<Create_VillaBookingAdditionalService_WC_MLS_XAction> additionalservices;

}
