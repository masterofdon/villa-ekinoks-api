package com.villaekinoks.app.registration.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Post_VillaOwnerUser_SetPasswordFirstTime_XAction {

  private String passwordsetrequestid;

  private String owneruserlogin;

  private String newpassword;

  private String confirmpassword;

}
