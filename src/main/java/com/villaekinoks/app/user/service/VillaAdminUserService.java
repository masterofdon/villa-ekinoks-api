package com.villaekinoks.app.user.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.user.VillaAdminUser;
import com.villaekinoks.app.user.repository.VillaAdminUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaAdminUserService {

  private final VillaAdminUserRepository villaAdminUserRepository;

  public VillaAdminUser getById(String id) {
    return villaAdminUserRepository.findById(id).orElse(null);
  }

  public VillaAdminUser getByLogin(String login) {
    return villaAdminUserRepository.findByLogin(login);
  }

  public VillaAdminUser create(VillaAdminUser user) {
    return villaAdminUserRepository.save(user);
  }

  public void delete(VillaAdminUser user) {
    villaAdminUserRepository.delete(user);
  }
}
