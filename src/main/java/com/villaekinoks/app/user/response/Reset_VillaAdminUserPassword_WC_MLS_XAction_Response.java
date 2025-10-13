package com.villaekinoks.app.user.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Reset_VillaAdminUserPassword_WC_MLS_XAction_Response {

  private String id;
}
