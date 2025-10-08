package com.villaekinoks.app.availability.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.generic.entity.Price;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceItem {

  private String name;

  private Integer quantity;

  private String unit;

  private Price price;
}
