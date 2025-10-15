package com.villaekinoks.app.discount;

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
public class DiscountCodeTimestamps {

  @Id
  @UuidGenerator
  private String id;
  
  @OneToOne
  @JoinColumn(name = "code_id", nullable = false)
  @JsonIgnore
  private DiscountCode code;

  private Long creationdate;

  private Long expirationdate;
}
