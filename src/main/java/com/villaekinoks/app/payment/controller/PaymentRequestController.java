package com.villaekinoks.app.payment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.payment.xaction.Post_PaymentProcessing_EXT_MLS_XAction;

@RestController
@RequestMapping("/api/v1/payment-requests")
public class PaymentRequestController {

  @PostMapping("/payment-processing-response")
  public String createPaymentRequest(
      @RequestBody Post_PaymentProcessing_EXT_MLS_XAction xAction) {
    return "OK";
  }
}
