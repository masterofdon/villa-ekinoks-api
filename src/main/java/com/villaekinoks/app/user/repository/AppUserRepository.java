package com.villaekinoks.app.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.user.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

  AppUser findByLogin(String login);
}
