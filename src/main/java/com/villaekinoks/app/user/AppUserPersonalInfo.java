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
public class AppUserPersonalInfo {

  @Id
  @UuidGenerator
  private String id;

  private String firstname;

  private String middlename;

  private String lastname;

  private String displayname;

  private String email;

  private String phonenumber;

  @OneToOne
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private AppUser user;
}
