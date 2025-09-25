package com.villaekinoks.app.calendar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.villaekinoks.app.calendar.CalendarEvent;
import com.villaekinoks.app.calendar.repository.CalendarEventRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarEventService {

  private final CalendarEventRepo eventRepo;

  public CalendarEvent getById(String id) {
    return eventRepo.findById(id).orElse(null);
  }

  public Page<CalendarEvent> getAll(String calendarId, Pageable pageable) {
    return eventRepo.findAllByCalendarId(calendarId, pageable);
  }

  public CalendarEvent create(CalendarEvent event) {
    return eventRepo.save(event);
  }

  public void delete(CalendarEvent event) {
    eventRepo.delete(event);
  }
}
