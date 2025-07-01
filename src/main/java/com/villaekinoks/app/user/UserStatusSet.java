package com.villaekinoks.app.user;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class UserStatusSet {

  @Id
  @UuidGenerator
  private String id;

  @OneToOne
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private AppUser user;

  @Enumerated(EnumType.STRING)
  private VerificationStatus verificationstatus;

  @Enumerated(EnumType.STRING)
  private ServiceStatus servicestatus;

  @Enumerated(EnumType.STRING)
  private OperationStatus operationstatus;

  @Enumerated(EnumType.STRING)
  private LockStatus lockstatus;

}
