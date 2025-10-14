package com.villaekinoks.app.appfile.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.appfile.AppFile;

public interface AppFileRepository extends JpaRepository<AppFile, String> {

}
