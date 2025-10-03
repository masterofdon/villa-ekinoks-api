package com.villaekinoks.app.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.payment.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {

}
