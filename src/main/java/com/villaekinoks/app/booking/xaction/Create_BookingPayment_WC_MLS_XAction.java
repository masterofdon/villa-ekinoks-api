package com.villaekinoks.app.booking.xaction;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_BookingPayment_WC_MLS_XAction {

  private String discountcode;

  private String cardholdername;

  private String cardnumber;

  private String cardexpirymonth;

  private String cardexpiryyear;

  private String cardcvc;

  private String useraddress;

  private String usercity;

  private String usercountry;

  private String userpostcode;

  private Set<Create_VillaBookingAdditionalService_WC_MLS_XAction> additionalservices;

}
