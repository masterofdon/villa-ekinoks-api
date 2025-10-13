package com.villaekinoks.app.anomaly;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;
import com.villaekinoks.app.generic.entity.Currency;
import com.villaekinoks.app.user.AppUser;
import com.villaekinoks.app.user.AppUserLocaleSettings;
import com.villaekinoks.app.user.service.AppUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/anomaly")
@RequiredArgsConstructor
public class AnomalyCorrectorController {

  private final AppUserService appUserService;

  @PutMapping("/correct-appuser-local-settings")
  public GenericApiResponse<Void> correctAppUserLocalSettings() {

    Page<AppUser> users = this.appUserService.getAll(Pageable.unpaged());
    for (AppUser user : users) {
      if (user.getLocalesettings() == null) {
        AppUserLocaleSettings localeSettings = new AppUserLocaleSettings();
        localeSettings.setLocale("en_UK");
        localeSettings.setCurrency(Currency.EUR);
        localeSettings.setUser(user);
        user.setLocalesettings(localeSettings);
        this.appUserService.save(user);
      }
    }
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS,
        "9491513");
  }
}
