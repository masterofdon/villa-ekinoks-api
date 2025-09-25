package com.villaekinoks.app.user.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.user.AppUserPersonalInfo;
import com.villaekinoks.app.user.AppUserTimeStamps;
import com.villaekinoks.app.user.LockStatus;
import com.villaekinoks.app.user.OperationStatus;
import com.villaekinoks.app.user.ServiceStatus;
import com.villaekinoks.app.user.UserStatusSet;
import com.villaekinoks.app.user.VerificationStatus;
import com.villaekinoks.app.user.VillaAdminUser;
import com.villaekinoks.app.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaOwnerRegistrationService {

  private final VillaAdminUserService villaAdminUserService;

  public VillaAdminUser startVillaOwnerRegistration(
      String login,
      String firstname,
      String middlename,
      String lastname,
      String displayname,
      String phonenumber,
      String email) {

    VillaAdminUser owner = new VillaAdminUser();
    owner.setLogin(email);

    UserStatusSet statusSet = new UserStatusSet();
    statusSet.setLockstatus(LockStatus.LOCKED);
    statusSet.setOperationstatus(OperationStatus.DISABLED);
    statusSet.setServicestatus(ServiceStatus.INACTIVE);
    statusSet.setVerificationstatus(VerificationStatus.PENDING);
    statusSet.setUser(owner);

    owner.setStatusset(statusSet);

    AppUserPersonalInfo personalinfo = new AppUserPersonalInfo();
    personalinfo.setFirstname(firstname);
    personalinfo.setMiddlename(middlename);
    personalinfo.setLastname(lastname);
    personalinfo.setEmail(email);
    personalinfo.setDisplayname(displayname);
    personalinfo.setPhonenumber(phonenumber);
    personalinfo.setUser(owner);

    owner.setPersonalinfo(personalinfo);

    AppUserTimeStamps timeStamps = new AppUserTimeStamps();
    timeStamps.setCreationdate(TimeUtils.tsInstantNow().toEpochMilli());
    timeStamps.setUser(owner);

    owner.setTimestamps(timeStamps);

    return this.villaAdminUserService.create(owner);
  }
}
