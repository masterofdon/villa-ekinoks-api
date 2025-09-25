package com.villaekinoks.app.user.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.user.AppUserPersonalInfo;
import com.villaekinoks.app.user.AppUserTimeStamps;
import com.villaekinoks.app.user.LockStatus;
import com.villaekinoks.app.user.OperationStatus;
import com.villaekinoks.app.user.ServiceStatus;
import com.villaekinoks.app.user.SystemAdminUser;
import com.villaekinoks.app.user.UserStatusSet;
import com.villaekinoks.app.user.VerificationStatus;
import com.villaekinoks.app.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemAdminUserRegistrationService {

  private final SystemAdminUserService systemAdminUserService;

  public SystemAdminUser registerSystemAdminUser(
      String login,
      String password,
      String firstname,
      String middlename,
      String lastname,
      String displayname,
      String phonenumber,
      String email) {

    SystemAdminUser sAdminUser = new SystemAdminUser();
    sAdminUser.setLogin(login);
    sAdminUser.setPassword(password);

    AppUserPersonalInfo personalinfo = new AppUserPersonalInfo();
    personalinfo.setFirstname(firstname);
    personalinfo.setMiddlename(middlename);
    personalinfo.setLastname(lastname);
    personalinfo.setDisplayname(displayname);
    personalinfo.setPhonenumber(phonenumber);
    personalinfo.setEmail(email);
    personalinfo.setUser(sAdminUser);

    sAdminUser.setPersonalinfo(personalinfo);

    UserStatusSet statusSet = new UserStatusSet();
    statusSet.setLockstatus(LockStatus.UNLOCKED);
    statusSet.setOperationstatus(OperationStatus.ENABLED);
    statusSet.setServicestatus(ServiceStatus.ACTIVE);
    statusSet.setVerificationstatus(VerificationStatus.VERIFIED);
    statusSet.setUser(sAdminUser);

    sAdminUser.setStatusset(statusSet);

    AppUserTimeStamps timeStamps = new AppUserTimeStamps();
    timeStamps.setCreationdate(TimeUtils.tsInstantNow().toEpochMilli());
    timeStamps.setVerificationdate(TimeUtils.tsInstantNow().toEpochMilli());
    timeStamps.setLastupdate(TimeUtils.tsInstantNow().toEpochMilli());
    timeStamps.setUser(sAdminUser);

    sAdminUser.setTimestamps(timeStamps);

    return this.systemAdminUserService.create(sAdminUser);
  }
}
