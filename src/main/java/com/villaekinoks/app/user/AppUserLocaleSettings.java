package com.villaekinoks.app.user;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villaekinoks.app.generic.entity.Currency;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class AppUserLocaleSettings {

  @Id
  @UuidGenerator
  private String id;

  @OneToOne
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private AppUser user;

  private String locale = "en_UK";

  private Currency currency = Currency.EUR;
}
