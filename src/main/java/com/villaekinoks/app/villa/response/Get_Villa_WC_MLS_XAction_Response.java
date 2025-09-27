package com.villaekinoks.app.villa.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.villaekinoks.app.user.SystemAdminUser;
import com.villaekinoks.app.user.VillaAdminUser;
import com.villaekinoks.app.villa.Villa;
import com.villaekinoks.app.villa.VillaPrivateInfo;
import com.villaekinoks.app.villa.VillaPublicInfo;
import com.villaekinoks.app.villapricing.VillaPricingSchema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Get_Villa_WC_MLS_XAction_Response {

  private String id;

  private VillaPublicInfo publicinfo;

  private VillaPrivateInfo privateinfo;

  private VillaPricingSchema pricing;

  private VillaAdminUser owner;

  @JsonIncludeProperties({ "id", "personalinfo" })
  private SystemAdminUser createdby;

  public Get_Villa_WC_MLS_XAction_Response(Villa villa) {
    this.id = villa.getId();
    this.publicinfo = villa.getPublicinfo();
    this.privateinfo = villa.getPrivateinfo();
    this.pricing = villa.getPricing();
    this.owner = villa.getOwner();
    this.createdby = villa.getCreatedby();
  }
}
