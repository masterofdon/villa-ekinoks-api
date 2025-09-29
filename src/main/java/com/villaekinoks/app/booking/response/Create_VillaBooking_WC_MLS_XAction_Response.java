package com.villaekinoks.app.booking.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.payment.view.PaymentRequestWCView;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaBooking_WC_MLS_XAction_Response {

  private String id;

  private PaymentRequestWCView paymentrequest;
}
