package com.villaekinoks.app.villa;

import org.hibernate.annotations.UuidGenerator;

import com.villaekinoks.app.user.VillaAdminUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaOperator {

  @Id
  @UuidGenerator
  private String id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private VillaAdminUser user;

  @ManyToOne
  @JoinColumn(name = "villa_id", nullable = false)
  private Villa villa;

  @OneToOne(mappedBy = "operator", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private VillaOperatorPrivileges privileges;
}
