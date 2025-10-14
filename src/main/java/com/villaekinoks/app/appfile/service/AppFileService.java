package com.villaekinoks.app.appfile.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.appfile.AppFile;
import com.villaekinoks.app.appfile.repository.AppFileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppFileService {

  private final AppFileRepository appFileRepository;

  public AppFile getById(String id) {
    return this.appFileRepository.findById(id).orElse(null);
  }

  public AppFile create(AppFile appFile) {
    return this.appFileRepository.save(appFile);
  }

  public void delete(AppFile file){
    this.appFileRepository.delete(file);
  }
}
