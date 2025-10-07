package com.villaekinoks.app.availability.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.generic.entity.Price;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class DateAvailability {

  private String date;

  private DateAvailabilityStatus status;

  private Boolean available;

  private Price price;
}
