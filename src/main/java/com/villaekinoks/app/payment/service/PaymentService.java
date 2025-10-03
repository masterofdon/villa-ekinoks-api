package com.villaekinoks.app.payment.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.payment.Payment;
import com.villaekinoks.app.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public Payment getById(String id) {
    return this.paymentRepository.findById(id).orElse(null);
  }

  public Payment create(Payment payment) {
    return this.paymentRepository.save(payment);
  }

  public void delete(Payment payment) {
    this.paymentRepository.delete(payment);
  }
}
