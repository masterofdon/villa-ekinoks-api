package com.villaekinoks.app.discount.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.discount.DiscountCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Get_DiscountCode_WC_MLS_XAction_Response {

  private List<DiscountCode> codes;
}
