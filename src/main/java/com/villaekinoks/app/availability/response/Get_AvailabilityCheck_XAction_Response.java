package com.villaekinoks.app.availability.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.availability.view.DateAvailability;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Get_AvailabilityCheck_XAction_Response {

  private List<DateAvailability> dateavailabilities;

  private Boolean allavailable;

  private String nextavailablestartdate;

  private String nextavailableenddate;

}
