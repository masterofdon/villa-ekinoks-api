package com.villaekinoks.app.servicableitem;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.generic.entity.Price;
import com.villaekinoks.app.villa.Villa;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ServicableItem {

  @Id
  @UuidGenerator
  private String id;

  @ManyToOne
  @JoinColumn(name = "villa_id", nullable = false)
  @JsonIgnore
  private Villa villa;

  @Enumerated(EnumType.STRING)
  private ServicableItemStatus status;

  private String name;

  private String description;

  private String iconlink;

  @Embedded
  private Price price;

  private String unit;

  private Integer minimum;

  private Integer maximum;

}
