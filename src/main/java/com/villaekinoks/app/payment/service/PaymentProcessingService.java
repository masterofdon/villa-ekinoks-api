package com.villaekinoks.app.payment.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.booking.VillaBooking;
import com.villaekinoks.app.booking.service.VillaBookingService;
import com.villaekinoks.app.exception.NotFoundException;
import com.villaekinoks.app.payment.PaymentRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

  private final PaymentRequestService paymentRequestService;

  private final VillaBookingService villaBookingService;

  @Transactional
  public PaymentRequest createPaymentRequestForBooking(
      String bookingid,
      String email,
      String userip,
      String amount,
      String currency,
      Integer installmentcount,
      Integer securepayment) {

    VillaBooking booking = this.villaBookingService.getById(bookingid);
    if (booking == null) {
      throw new NotFoundException();
    }

    PaymentRequest paymentRequest = new PaymentRequest();
    paymentRequest.setBooking(booking);
    paymentRequest.setEmail(email);
    paymentRequest.setUserip(userip);
    paymentRequest.setPaymenttype("card");
    paymentRequest.setAmount(amount);
    paymentRequest.setCurrency(currency);
    paymentRequest.setInstallmentcount(installmentcount);
    paymentRequest.setSecurepayment(securepayment);
    paymentRequest = paymentRequestService.create(paymentRequest);

    String merchantKey = "123456";
    String merchantSalt = "abcdef";

    // create payment request token for PayTR

    String hashStr = "_merchantId" + userip + paymentRequest.getId() + email + amount + "card" + installmentcount
        + currency
        + "0" + securepayment + merchantSalt;
    try {
      Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secret_key = new SecretKeySpec(merchantKey.getBytes("UTF-8"), "HmacSHA256");
      sha256_HMAC.init(secret_key);
      byte[] hashBytes = sha256_HMAC.doFinal(hashStr.getBytes("UTF-8"));
      String token = java.util.Base64.getEncoder().encodeToString(hashBytes);
      paymentRequest.setExternaltoken(token);
    } catch (Exception e) {
      throw new RuntimeException("Error generating payment token", e);
    }

    paymentRequest = paymentRequestService.create(paymentRequest);

    return paymentRequest;
  }
}
