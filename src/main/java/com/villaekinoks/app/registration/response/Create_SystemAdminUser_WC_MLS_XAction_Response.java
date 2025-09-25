package com.villaekinoks.app.registration.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@AllArgsConstructor
public class Create_SystemAdminUser_WC_MLS_XAction_Response {
  
  private String id;

  private String message;

}