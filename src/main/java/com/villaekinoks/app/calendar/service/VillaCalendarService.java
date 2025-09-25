package com.villaekinoks.app.calendar.service;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.calendar.VillaCalendar;
import com.villaekinoks.app.calendar.repository.VillaCalendarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillaCalendarService {

  private final VillaCalendarRepository villaCalendarRepository;

  public VillaCalendar getById(String id) {
    return villaCalendarRepository.findById(id).orElse(null);
  }

  public VillaCalendar getByVillaId(String villaId) {
    return villaCalendarRepository.findById(villaId).orElse(null);
  }

  public VillaCalendar create(VillaCalendar villaCalendar) {
    return villaCalendarRepository.save(villaCalendar);
  }

  public void delete(VillaCalendar calendar){
    villaCalendarRepository.delete(calendar);
  }
}
