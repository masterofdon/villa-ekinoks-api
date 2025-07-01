package com.villaekinoks.app.appfile;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class AppFile {

  @Id
  @UuidGenerator
  private String id;

  private String filename;

  private String filetype;

}
