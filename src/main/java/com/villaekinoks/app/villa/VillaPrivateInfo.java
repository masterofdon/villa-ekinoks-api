package com.villaekinoks.app.villa;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaPrivateInfo {

  @Id
  @UuidGenerator
  private String id;

  @OneToOne(mappedBy = "villaprivateinfo", cascade = CascadeType.REMOVE)
  private VillaAddress address;

  @OneToOne
  @JoinColumn(name = "villa_id", nullable = false)
  private Villa villa;
}
