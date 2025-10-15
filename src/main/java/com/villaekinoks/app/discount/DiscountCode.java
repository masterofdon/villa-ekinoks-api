package com.villaekinoks.app.discount;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.villaekinoks.app.user.VillaAdminUser;
import com.villaekinoks.app.villa.Villa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class DiscountCode {

  @Id
  @UuidGenerator
  private String id;

  private String code;

  @ManyToOne
  @JoinColumn(name = "villa_id", nullable = false)
  @JsonIgnore
  private Villa villa;

  @ManyToOne
  @JoinColumn(name = "createdby_id", nullable = false)
  @JsonIncludeProperties({ "id", "personalinfo" })
  private VillaAdminUser createdby;

  @OneToOne(mappedBy = "code", cascade = CascadeType.ALL, orphanRemoval = true)
  private DiscountCodeTimestamps timestamps;

  @Enumerated(EnumType.STRING)
  private DiscountType discounttype;

  private String value;

  @Enumerated(EnumType.STRING)
  private DiscountCodeStatus status;

}
