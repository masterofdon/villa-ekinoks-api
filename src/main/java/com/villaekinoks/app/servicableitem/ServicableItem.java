package com.villaekinoks.app.servicableitem;

import org.hibernate.annotations.UuidGenerator;

import com.villaekinoks.app.generic.entity.Price;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ServicableItem {

  @Id
  @UuidGenerator
  private String id;

  private String name;

  private String description;

  private String iconlink;

  @Embedded
  private Price price;

  private String unit;

}
