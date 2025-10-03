package com.villaekinoks.app.user.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.user.VillaGuestUser;
import com.villaekinoks.app.user.repository.VillaGuestUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaGuestUserService {

  private final VillaGuestUserRepository villaGuestUserRepository;

  public VillaGuestUser getById(String id) {
    return this.villaGuestUserRepository.findById(id).orElse(null);
  }

  public VillaGuestUser getByLogin(String login) {
    return this.villaGuestUserRepository.findByLogin(login);
  }

  public VillaGuestUser create(VillaGuestUser user) {
    return this.villaGuestUserRepository.save(user);
  }

  public void delete(VillaGuestUser user) {
    this.villaGuestUserRepository.delete(user);
  }
}
