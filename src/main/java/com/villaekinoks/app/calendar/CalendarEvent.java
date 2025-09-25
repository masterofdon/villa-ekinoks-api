package com.villaekinoks.app.calendar;

import java.time.LocalDateTime;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class CalendarEvent {

  @Id
  @UuidGenerator
  private String id;

  @ManyToOne
  @JoinColumn(name = "calendar_id")
  @JsonIgnore
  private VillaCalendar calendar;

  @Column(length = 1024)
  private String summary;

  @Column(nullable = false)
  private LocalDateTime dtstartUtc;

  private LocalDateTime dtendUtc;

  @Column(length = 128)
  private String tzid;

  @Lob
  private String rrule;

  @Type(JsonType.class) // from hibernate-types, optional
  @Column(columnDefinition = "json")
  private String exdateJson;

  @Type(JsonType.class)
  @Column(columnDefinition = "json")
  private String rdateJson;

  @Column(length = 1024)
  private String location;

  private LocalDateTime lastModified;

  private Integer sequence = 0;

  private Integer version = 1;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

}
