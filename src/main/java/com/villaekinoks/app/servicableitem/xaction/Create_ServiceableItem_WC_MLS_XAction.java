package com.villaekinoks.app.servicableitem.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.generic.entity.Price;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_ServiceableItem_WC_MLS_XAction {

  private String villaid;

  private String name;

  private String description;

  private String iconlink;

  private String unit;

  private Price price;
}
