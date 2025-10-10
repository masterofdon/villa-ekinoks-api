package com.villaekinoks.app.villapricing.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class BookingInfo {

  private String startdate;

  private String enddate;
}
