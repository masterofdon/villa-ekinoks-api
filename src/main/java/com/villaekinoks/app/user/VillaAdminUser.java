package com.villaekinoks.app.user;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.villaekinoks.app.villa.Villa;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class VillaAdminUser extends AppUser {

  @ManyToOne
  @JoinColumn(name = "villa_id", nullable = false)
  @JsonIncludeProperties({ "id", "publicinfo", "privateinfo" })
  private Villa villa;

  @Enumerated(EnumType.STRING)
  private VillaAdminUserRegistrationStatus registrationstatus;

  public VillaAdminUser() {
    this.role = Role.VILLAADMIN;
  }
}
