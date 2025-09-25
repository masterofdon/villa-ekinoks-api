package com.villaekinoks.app.villa;

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
public class VillaOperatorPrivileges {
  
  @Id
  @UuidGenerator
  private String id;

  @OneToOne
  @JoinColumn(name = "operator_id", nullable = false)
  @JsonIgnore
  private VillaOperator operator;

  private Integer bookings;

  private Integer pricing;

  private Integer availability;

  private Integer publicinfo;

  private Integer privateinfo;

  private Integer reviews;

  private Integer messages;
}
