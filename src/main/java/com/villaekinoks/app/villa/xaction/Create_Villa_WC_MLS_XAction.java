package com.villaekinoks.app.villa.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.generic.entity.Address;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_Villa_WC_MLS_XAction {

  private String name;

  private String slug;

  private String promotext;

  private String description;

  private Address address;

  private String owneremail;

  private String ownerfirstname;

  private String ownermiddlename;

  private String ownerlastname;

  private String ownerdisplayname;

  private String ownerphonenumber;
}
