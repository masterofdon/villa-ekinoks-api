package com.villaekinoks.app.registration.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Create_SystemAdminUser_WC_MLS_XAction {

  private String login;

  private String password;

  private String email;

  private String firstname;

  private String middlename;

  private String lastname;

  private String phonenumber;

  private String secret;

}