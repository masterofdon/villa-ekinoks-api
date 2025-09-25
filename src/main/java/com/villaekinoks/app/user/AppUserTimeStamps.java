package com.villaekinoks.app.user;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class AppUserTimeStamps {

  @Id
  @UuidGenerator
  private String id;

  private Long creationdate;

  private Long verificationdate;

  private Long lastupdate;

  @OneToOne
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private AppUser user;
}
