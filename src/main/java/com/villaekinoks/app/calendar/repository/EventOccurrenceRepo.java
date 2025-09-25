package com.villaekinoks.app.calendar.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.villaekinoks.app.calendar.EventOccurrence;

public interface EventOccurrenceRepo extends JpaRepository<EventOccurrence, EventOccurrence.EventOccurrenceId> {
  @Query("""
         select o from EventOccurrence o
         where o.id.occurrenceStartUtc < :to and o.occurrenceEndUtc >= :from
         order by o.id.occurrenceStartUtc
      """)
  List<EventOccurrence> findInWindow(@Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);
}
