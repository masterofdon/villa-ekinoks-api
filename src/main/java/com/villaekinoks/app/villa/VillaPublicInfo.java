package com.villaekinoks.app.villa;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaPublicInfo {

  @Id
  @UuidGenerator
  private String id;

  private String name;

  private String slug;

  @Column(length = 3200)
  private String promotext;

  @Column(length = 3200)
  private String description;

  @OneToOne
  @JoinColumn(name = "villa_id", nullable = false)
  private Villa villa;
}
