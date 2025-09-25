package com.villaekinoks.app.verification;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VerificationPair {

  @Id
  @UuidGenerator
  private String id;

  private String userid;

  private String verificationcode;

  private Long creationdate;

  private Long expirationdate;

  @Enumerated(EnumType.STRING)
  private VerificationPairStatus status;
}
