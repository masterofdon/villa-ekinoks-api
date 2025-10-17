package com.villaekinoks.app.registration.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.villaekinoks.app.user.VillaGuestUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Create_VillaGuestUserRegistrationVerification_WC_MLS_XAction_Response {

  @JsonIncludeProperties({ "id", "personalinfo" })
  private VillaGuestUser user;
}
