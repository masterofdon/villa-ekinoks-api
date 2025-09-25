package com.villaekinoks.app.generic.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
public class Price {
  
  private Double amount;

  @Enumerated(EnumType.STRING)
  private Currency currency;
}
