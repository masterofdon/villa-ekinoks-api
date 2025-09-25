package com.villaekinoks.app.generic.api;

public class GenericApiResponseCodes {

  public static class AuthenticationResponseCodes {

    public static final String USER_DISABLED = "403#1001";
    public static final String USER_ACCOUNT_LOCKED = "403#1002";
    public static final String BAD_CREDENTIALS = "403#1003";
  }

  public static class VillaController {

    public static final String CREATE_VILLA_SUCCESS = "201#2001";
    public static final String CREATE_VILLA_FAIL = "400#4001";
  }

  public static class SystemAdminUserRegistrationController {

    public static final String CREATE_USER_SUCCESS = "201#1000";
  }
}
