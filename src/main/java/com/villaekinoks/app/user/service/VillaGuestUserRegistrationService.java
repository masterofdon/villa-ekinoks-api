package com.villaekinoks.app.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.villaekinoks.app.generic.entity.Currency;
import com.villaekinoks.app.generic.entity.DeleteStatus;
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

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

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

    VillaGuestUser vGuestUser = new VillaGuestUser();
    vGuestUser.setLogin(email);
    vGuestUser.setPassword(bCryptPasswordEncoder.encode(password));
    vGuestUser.setDeletestatus(DeleteStatus.NOTDELETED);

    AppUserPersonalInfo personalInfo = new AppUserPersonalInfo();
    personalInfo.setFirstname(firstname);
    personalInfo.setMiddlename(middlename);
    personalInfo.setLastname(lastname);
    personalInfo.setEmail(email);
    personalInfo.setPhonenumber(phonenumber);
    personalInfo.setUser(vGuestUser);
    vGuestUser.setPersonalinfo(personalInfo);

    AppUserLocaleSettings localeSettings = new AppUserLocaleSettings();
    localeSettings.setLocale(locale);
    localeSettings.setCurrency(currency);
    vGuestUser.setLocalesettings(localeSettings);

    AppUserTimeStamps timeStamps = new AppUserTimeStamps();
    timeStamps.setCreationdate(TimeUtils.tsInstantNow().toEpochMilli());
    timeStamps.setUser(vGuestUser);
    vGuestUser.setTimestamps(timeStamps);

    UserStatusSet statusSet = new UserStatusSet();
    statusSet.setLockstatus(LockStatus.LOCKED);
    statusSet.setOperationstatus(OperationStatus.DISABLED);
    statusSet.setServicestatus(ServiceStatus.INACTIVE);
    statusSet.setVerificationstatus(VerificationStatus.PENDING);
    statusSet.setUser(vGuestUser);
    vGuestUser.setStatusset(statusSet);

    vGuestUser = this.villaGuestUserService.create(vGuestUser);

    return vGuestUser;
  }
}
