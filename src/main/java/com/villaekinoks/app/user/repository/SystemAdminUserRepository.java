package com.villaekinoks.app.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.user.SystemAdminUser;

public interface SystemAdminUserRepository extends JpaRepository<SystemAdminUser, String> {

  SystemAdminUser findByLogin(String login);
}
