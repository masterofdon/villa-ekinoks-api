package com.villaekinoks.app.verification.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.verification.VerificationPair;
import com.villaekinoks.app.verification.repository.VerificationPairRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationPairService {
  
  private final VerificationPairRepository verificationPairRepository;

  public VerificationPair getById(String id){
    return verificationPairRepository.findById(id).orElse(null);
  }

  public VerificationPair create(VerificationPair pair){
    return verificationPairRepository.save(pair);
  }

  public void delete(VerificationPair pair){
    verificationPairRepository.delete(pair);
  }
}
