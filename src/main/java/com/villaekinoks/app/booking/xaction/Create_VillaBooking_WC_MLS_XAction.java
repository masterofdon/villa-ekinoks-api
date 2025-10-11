package com.villaekinoks.app.booking.xaction;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.generic.entity.Currency;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaBooking_WC_MLS_XAction {

  private String villaid;

  private Integer numberofguests = -1;

  private String startdate;

  private String enddate;

  private String inquiror_firstname;

  private String inquiror_middlename;

  private String inquiror_lastname;

  private String inquiror_identitynumber;

  private String inquiror_email;

  private String inquiror_phonenumber;

  private String inquiror_locale;

  private Currency inquiror_currency;

  private List<Create_VillaBookingAdditionalService_WC_MLS_XAction> additionalservices;

}
