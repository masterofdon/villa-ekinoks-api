package com.villaekinoks.app.user;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

  private Long lastupdate;
}
