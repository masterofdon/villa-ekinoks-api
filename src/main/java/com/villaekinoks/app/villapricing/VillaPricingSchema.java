package com.villaekinoks.app.villapricing;

import java.util.Set;

import org.hibernate.annotations.UuidGenerator;

import com.villaekinoks.app.user.AppUser;
import com.villaekinoks.app.villa.Villa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaPricingSchema {

  @Id
  @UuidGenerator
  private String id;

  private String name;

  @Enumerated(EnumType.STRING)
  private PricingStatus status;

  @ManyToOne
  @JoinColumn(name = "villa_id", nullable = false)
  private Villa villa;

  @ManyToOne
  @JoinColumn(name = "created_by")
  private AppUser createdby;

  private String pricepernight;

  private String longtermdiscount;

  @OneToMany(mappedBy = "villapricingschema", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
  private Set<PricingRange> pricingranges;
}
