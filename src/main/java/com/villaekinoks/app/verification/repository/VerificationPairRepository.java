package com.villaekinoks.app.verification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.verification.VerificationPair;

public interface VerificationPairRepository extends JpaRepository<VerificationPair, String> {

}
