package com.villaekinoks.app.user.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.user.AppUser;
import com.villaekinoks.app.user.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserService {
  
  private AppUserRepository appUserRepository;

  public AppUser getById(String id){
    return appUserRepository.findById(id).orElse(null);
  }

  public AppUser getByLogin(String login){
    return appUserRepository.findByLogin(login);
  }

  public AppUser save(AppUser user){
    return appUserRepository.save(user);
  }

  public void delete(AppUser user){
    appUserRepository.delete(user);
  }

}
