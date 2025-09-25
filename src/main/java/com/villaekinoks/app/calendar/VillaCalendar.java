package com.villaekinoks.app.calendar;

import org.hibernate.annotations.UuidGenerator;

import com.villaekinoks.app.villa.Villa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class VillaCalendar {
  
  @Id
  @UuidGenerator
  private String id;

  @ManyToOne
  @JoinColumn(name = "villa_id")
  private Villa villa; 

  @Column(name = "icalendar_str" , nullable = false , length = 3000)
  private String icalendarstr;
}
