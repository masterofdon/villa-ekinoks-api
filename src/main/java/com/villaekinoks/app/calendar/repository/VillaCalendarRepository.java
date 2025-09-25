package com.villaekinoks.app.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.villaekinoks.app.calendar.VillaCalendar;

public interface VillaCalendarRepository extends JpaRepository<VillaCalendar, String>{
  
}
