package com.villaekinoks.app.appfile;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  private Long creationdate;

  private String name;

  private String url;

  private String extension;

  private String mimetype;

  @Enumerated(EnumType.STRING)
  private AppFileType type;

  private String uploaderid;

}
