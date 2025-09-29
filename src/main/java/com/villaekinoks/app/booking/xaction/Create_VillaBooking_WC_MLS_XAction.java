package com.villaekinoks.app.booking.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaBooking_WC_MLS_XAction {

  private String villaid;

  private String startdate;

  private String enddate;

  private String inquiror_firstname;

  private String inquiror_middlename;

  private String inquiror_lastname;

  private String inquiror_email;

  private String inquiror_phonenumber;
}
