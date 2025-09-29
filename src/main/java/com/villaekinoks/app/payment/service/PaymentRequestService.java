package com.villaekinoks.app.payment.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.payment.PaymentRequest;
import com.villaekinoks.app.payment.repository.PaymentRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentRequestService {

  private final PaymentRequestRepository paymentRequestRepository;

  public PaymentRequest getById(String id) {
    return paymentRequestRepository.findById(id).orElse(null);
  }

  public PaymentRequest getByExternalId(String externalid) {
    return paymentRequestRepository.findByExternalid(externalid);
  }

  public Set<PaymentRequest> getByBookingId(String bookingid) {
    return paymentRequestRepository.findByBookingId(bookingid);
  }

  public PaymentRequest create(PaymentRequest paymentRequest) {
    return paymentRequestRepository.save(paymentRequest);
  }

  public void delete(PaymentRequest paymentRequest) {
    paymentRequestRepository.delete(paymentRequest);
  }
}
