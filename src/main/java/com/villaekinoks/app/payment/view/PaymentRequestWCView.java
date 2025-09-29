package com.villaekinoks.app.payment.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.payment.PaymentRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestWCView {

  private String id;

  private Long creationdate;

  private String userip;

  private String email;

  private String amount;

  private String currency;

  private Integer installmentcount;

  private Integer securepayment;

  private String paymenttype;

  private String externaltoken;

  public static PaymentRequestWCView fromEntity(PaymentRequest entity) {
    if (entity == null) {
      return null;
    }
    PaymentRequestWCView view = new PaymentRequestWCView();
    view.setId(entity.getId());
    view.setCreationdate(entity.getCreationdate());
    view.setUserip(entity.getUserip());
    view.setEmail(entity.getEmail());
    view.setAmount(entity.getAmount());
    view.setCurrency(entity.getCurrency());
    view.setInstallmentcount(entity.getInstallmentcount());
    view.setSecurepayment(entity.getSecurepayment());
    view.setPaymenttype(entity.getPaymenttype());
    view.setExternaltoken(entity.getExternaltoken());
    return view;
  }
}
