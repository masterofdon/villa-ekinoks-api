package com.villaekinoks.app.calendar.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.villaekinoks.app.calendar.CalendarEvent;

public interface CalendarEventRepo extends JpaRepository<CalendarEvent, String> {

  Page<CalendarEvent> findAllByCalendarId(String calendarId, Pageable pageable);

  @Query("""
select e from CalendarEvent e
         where e.dtendUtc >= :from and e.dtstartUtc < :to
      """)
  List<CalendarEvent> findActiveInWindow(@Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);
}
