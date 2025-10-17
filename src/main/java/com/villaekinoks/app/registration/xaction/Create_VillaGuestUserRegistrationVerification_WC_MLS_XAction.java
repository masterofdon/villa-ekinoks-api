package com.villaekinoks.app.registration.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Create_VillaGuestUserRegistrationVerification_WC_MLS_XAction {

  private String registrationverificationid;

  private String code;
}
