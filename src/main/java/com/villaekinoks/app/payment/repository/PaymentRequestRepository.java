package com.villaekinoks.app.payment.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.payment.PaymentRequest;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, String> {

  PaymentRequest findByExternalid(String externalid);

  Set<PaymentRequest> findByBookingId(String bookingid);
}
