package com.villaekinoks.app.availability.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.booking.xaction.Create_VillaBookingAdditionalService_WC_MLS_XAction;
import com.villaekinoks.app.generic.entity.Currency;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Post_ItemPricesBreakdown_XAction {

  private String villaid;

  private String startdate;

  private String enddate;

  private Currency currency;

  private Create_VillaBookingAdditionalService_WC_MLS_XAction[] additionalservices;
}
