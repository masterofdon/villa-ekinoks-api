package com.villaekinoks.app.user.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.user.SystemAdminUser;
import com.villaekinoks.app.user.repository.SystemAdminUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemAdminUserService {

  private final SystemAdminUserRepository systemAdminUserRepository;

  public SystemAdminUser getById(String id) {
    return this.systemAdminUserRepository.findById(id).orElse(null);
  }

  public SystemAdminUser getByLogin(String login) {
    return this.systemAdminUserRepository.findByLogin(login);
  }

  public SystemAdminUser create(SystemAdminUser sUser) {
    return this.systemAdminUserRepository.save(sUser);
  }

  public void delete(SystemAdminUser sUser) {
    this.systemAdminUserRepository.delete(sUser);
  }
}
