package com.villaekinoks.app.discount.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.discount.DiscountCodeStatus;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Update_DiscountCodeStatus_WC_MLS_XAction {
  
  private DiscountCodeStatus status;
}
