package com.villaekinoks.app.payment.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Post_PaymentProcessing_EXT_MLS_XAction {

  private String merchant_oid;

  private String status;

  // multiplied by 100
  private Integer total_amount;

  private String hash;

  private String failed_reason_code;

  private String failed_reason_msg;

  private Integer test_mode;

  private String payment_type;

  private String currency;

  // multiplied by 100
  private Integer payment_amount;

  private Integer installment_count;
}
