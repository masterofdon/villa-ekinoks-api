package com.villaekinoks.app.user;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class SystemAdminUser extends AppUser {

  public SystemAdminUser() {
    this.role = Role.SYSTEMADMIN;
  }
}
