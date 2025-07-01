package com.villaekinoks.app.booking;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.appfile.AppFile;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaBookingGuestPersonalInfo {

  @Id
  @UuidGenerator
  private String id;

  private String firstname;

  private String middlename;

  private String lastname;

  private String email;

  private String phonenumber;

  private String passportno;

  private String passportcountry;

  private Integer age;

  @ManyToOne
  @JoinColumn(name = "passportfrontfile_id", nullable = true)
  private AppFile passportfront;

  @ManyToOne
  @JoinColumn(name = "passportbackfile_id", nullable = true)
  private AppFile passportback;

  @ManyToOne
  @JoinColumn(name = "guest_id", nullable = false)
  @JsonIgnore
  private VillaBookingGuest guest;
}
