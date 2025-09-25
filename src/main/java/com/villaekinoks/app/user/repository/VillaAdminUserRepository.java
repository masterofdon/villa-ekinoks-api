package com.villaekinoks.app.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.user.VillaAdminUser;

public interface VillaAdminUserRepository extends JpaRepository<VillaAdminUser, String> {
  
  VillaAdminUser findByLogin(String login);
  
}
