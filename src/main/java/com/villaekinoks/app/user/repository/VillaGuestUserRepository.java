package com.villaekinoks.app.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.user.VillaGuestUser;

public interface VillaGuestUserRepository extends JpaRepository<VillaGuestUser, String> {

  VillaGuestUser findByLogin(String login);
}
