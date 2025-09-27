package com.villaekinoks.app.verification.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Verify_LoginVerification_XAction {
  
  private String requestid;

  private String code;
}
