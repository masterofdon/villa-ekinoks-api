package com.villaekinoks.app.villapricing.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.villapricing.VillaPricingSchema;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class VillaPricingSchemaWithBookings {

  private VillaPricingSchema pricing;

  private List<BookingInfo> bookings;
}
