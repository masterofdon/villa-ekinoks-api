package com.villaekinoks.app.user.xaction;

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
public class Reset_VillaAdminUserPassword_WC_MLS_XAction {

  private String email;

  private String newpassword;
}
