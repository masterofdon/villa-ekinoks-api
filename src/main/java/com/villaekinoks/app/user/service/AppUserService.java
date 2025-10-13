package com.villaekinoks.app.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.villaekinoks.app.user.AppUser;
import com.villaekinoks.app.user.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserService {

  private final AppUserRepository appUserRepository;

  public AppUser getById(String id) {
    return appUserRepository.findById(id).orElse(null);
  }

  public AppUser getByLogin(String login) {
    return appUserRepository.findByLogin(login);
  }

  public Page<AppUser> getAll(Pageable pageable) {
    return appUserRepository.findAll(pageable);
  }

  public AppUser save(AppUser user) {
    return appUserRepository.save(user);
  }

  public void delete(AppUser user) {
    appUserRepository.delete(user);
  }

}
