package com.villaekinoks.app.user;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.villaekinoks.app.villa.Villa;

import jakarta.persistence.Entity;
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
  @JsonIncludeProperties({ "id", "name" })
  private Villa villa;

  public VillaAdminUser(String username, String password) {
    this.role = Role.VILLAADMIN;
  }
}
