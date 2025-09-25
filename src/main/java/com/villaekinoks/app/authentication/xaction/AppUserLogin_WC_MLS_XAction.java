package com.villaekinoks.app.authentication.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class AppUserLogin_WC_MLS_XAction {
  
  private String login;

  private String password;
}
