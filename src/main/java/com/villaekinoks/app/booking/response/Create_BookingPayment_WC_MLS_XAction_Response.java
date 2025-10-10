package com.villaekinoks.app.booking.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Create_BookingPayment_WC_MLS_XAction_Response {

  private String paymentid;

  private String bookingid;

  private String ackno;

}
