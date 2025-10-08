package com.villaekinoks.app.villa;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.generic.entity.Address;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne
  @JoinColumn(name = "address_id")
  private Address address;

  @OneToOne
  @JoinColumn(name = "villa_id", nullable = false)
  @JsonIgnore
  private Villa villa;
}
