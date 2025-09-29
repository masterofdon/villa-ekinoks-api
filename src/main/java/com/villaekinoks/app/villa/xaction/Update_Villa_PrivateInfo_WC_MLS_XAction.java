package com.villaekinoks.app.villa.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.generic.entity.Address;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Update_Villa_PrivateInfo_WC_MLS_XAction {

  private Address address;
}
