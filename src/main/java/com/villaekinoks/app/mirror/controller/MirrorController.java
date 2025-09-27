package com.villaekinoks.app.mirror.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.configuration.JwtService;
import com.villaekinoks.app.exception.NotAuthorizedException;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.generic.model.TokenizedUser;
import com.villaekinoks.app.user.AppUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/mirror")
@RequiredArgsConstructor
public class MirrorController {

  private final JwtService jwtService;

  @GetMapping
  public GenericApiResponse<TokenizedUser> mirror() {

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!(principal instanceof AppUser)) {
      throw new NotAuthorizedException();
    }
    AppUser user = (AppUser) principal;

    TokenizedUser tokenizedUser = new TokenizedUser();
    tokenizedUser.setUser(user);
    tokenizedUser.setAccesstoken(jwtService.generateAccessToken(user));
    tokenizedUser.setRefreshtoken(jwtService.generateRefreshToken(user));

    return new GenericApiResponse<TokenizedUser>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "200",
        tokenizedUser);
  }
}
