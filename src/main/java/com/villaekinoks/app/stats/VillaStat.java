package com.villaekinoks.app.stats;

import org.hibernate.annotations.UuidGenerator;

import com.villaekinoks.app.villa.Villa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaStat {

  @Id
  @UuidGenerator
  private String id;

  @ManyToOne
  @JoinColumn(name = "villa_id", nullable = false)
  private Villa villa;

  private Long lastupdate;

  private String statcode;

  private String value;

  private String prefix;

  private String suffix;

  private String color;
}
