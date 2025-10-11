package com.villaekinoks.app.user.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.generic.entity.Currency;
import com.villaekinoks.app.user.AppUserLocaleSettings;
import com.villaekinoks.app.user.AppUserPersonalInfo;
import com.villaekinoks.app.user.AppUserTimeStamps;
import com.villaekinoks.app.user.LockStatus;
import com.villaekinoks.app.user.OperationStatus;
import com.villaekinoks.app.user.ServiceStatus;
import com.villaekinoks.app.user.UserStatusSet;
import com.villaekinoks.app.user.VerificationStatus;
import com.villaekinoks.app.user.VillaGuestUser;
import com.villaekinoks.app.utils.TimeUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaGuestUserRegistrationService {

  private final VillaGuestUserService villaGuestUserService;

  @Transactional
  public VillaGuestUser registerNewUser(
      String login,
      String password,
      String firstname,
      String middlename,
      String lastname,
      String identitynumber,
      String email,
      String phonenumber,
      String locale,
      Currency currency) {

    VillaGuestUser inquiror = new VillaGuestUser();
    inquiror.setLogin(login);
    inquiror.setPassword(password); // No password for guest users
    inquiror.setIdentitynumber(identitynumber);

    AppUserTimeStamps userTimestamps = new AppUserTimeStamps();
    userTimestamps.setCreationdate(TimeUtils.tsInstantNow().toEpochMilli());
    userTimestamps.setUser(inquiror);

    AppUserLocaleSettings localeSettings = new AppUserLocaleSettings();
    localeSettings.setLocale(locale);
    localeSettings.setCurrency(currency);
    localeSettings.setUser(inquiror);

    inquiror.setTimestamps(userTimestamps);
    inquiror.setLocalesettings(localeSettings);

    UserStatusSet statusSet = new UserStatusSet();
    statusSet.setLockstatus(LockStatus.UNLOCKED);
    statusSet.setOperationstatus(OperationStatus.ENABLED);
    statusSet.setServicestatus(ServiceStatus.ACTIVE);
    statusSet.setVerificationstatus(VerificationStatus.VERIFIED);
    statusSet.setUser(inquiror);

    inquiror.setStatusset(statusSet);

    AppUserPersonalInfo personalInfo = new AppUserPersonalInfo();
    personalInfo.setFirstname(firstname);
    personalInfo.setMiddlename(middlename);
    personalInfo.setLastname(lastname);
    personalInfo.setEmail(email);
    personalInfo.setPhonenumber(phonenumber);
    personalInfo.setUser(inquiror);

    inquiror.setPersonalinfo(personalInfo);

    inquiror = this.villaGuestUserService.create(inquiror);

    return inquiror;
  }
}
