package com.villaekinoks.app.user;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class SystemAdminUser {

  @Id
  @UuidGenerator
  private String id;
}
