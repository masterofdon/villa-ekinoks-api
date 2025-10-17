package com.villaekinoks.app.registration.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.generic.entity.Currency;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_VillaGuestUser_WC_MLS_XAction {

  private String email;

  private String firstname;

  private String middlename;

  private String lastname;

  private String phonenumber;

  private String locale;

  private Currency currency;

  private String identitynumber;

}
