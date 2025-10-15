package com.villaekinoks.app.discount.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.discount.DiscountCodeUsageType;
import com.villaekinoks.app.discount.DiscountType;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_DiscountCode_WC_MLS_XAction {

  private DiscountCodeUsageType usagetype;

  private Long expirationdate;

  private String villaid;

  private DiscountType type;

  private String value;
}
