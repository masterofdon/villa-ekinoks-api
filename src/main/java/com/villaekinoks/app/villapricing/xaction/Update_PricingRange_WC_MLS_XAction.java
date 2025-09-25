package com.villaekinoks.app.villapricing.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.generic.entity.Price;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Update_PricingRange_WC_MLS_XAction {

  private UpdatePricingRangeAction action;

  private String startperiod;

  private String endperiod;

  private Price pricepernight;
}
