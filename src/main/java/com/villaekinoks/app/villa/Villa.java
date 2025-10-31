package com.villaekinoks.app.villa;

import java.util.Set;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.villaekinoks.app.user.SystemAdminUser;
import com.villaekinoks.app.user.VillaAdminUser;
import com.villaekinoks.app.villapricing.VillaPricingSchema;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Villa {

  @Id
  @UuidGenerator
  private String id;

  @OneToOne(mappedBy = "villa", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private VillaPublicInfo publicinfo;

  @OneToOne(mappedBy = "villa", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private VillaPrivateInfo privateinfo;

  @OneToMany(mappedBy = "villa", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  @JsonIgnore
  private Set<VillaOperator> operators;

  @OneToOne(mappedBy = "villa", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private VillaPricingSchema pricing;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private VillaAdminUser owner;

  @ManyToOne
  @JoinColumn(name = "creator_id")
  @JsonIncludeProperties({ "id", "personalinfo" })
  private SystemAdminUser createdby;

  @OneToMany(mappedBy = "villa", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  @JsonIgnore
  private Set<VillaFacilityItem> facilities;
}
