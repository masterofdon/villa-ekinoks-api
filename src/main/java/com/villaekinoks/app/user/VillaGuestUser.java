package com.villaekinoks.app.user;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class VillaGuestUser extends AppUser {

  private String identitynumber;

  public VillaGuestUser() {
    this.role = Role.GUEST;
  }
}
