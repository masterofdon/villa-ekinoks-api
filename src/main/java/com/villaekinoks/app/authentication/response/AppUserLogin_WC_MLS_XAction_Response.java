package com.villaekinoks.app.authentication.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class AppUserLogin_WC_MLS_XAction_Response {

  private String ackid;

  private String requestid;
}