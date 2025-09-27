package com.villaekinoks.app.generic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.user.AppUser;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class TokenizedUser {

  private String accesstoken;

  private String refreshtoken;

  @JsonIgnoreProperties({ "timestamps", "statusset", "lastreqtime", "isonline", "role", "username" })
  private AppUser user;
}
