package com.villaekinoks.app.calendar;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class EventOccurrence {

  @EmbeddedId
  private EventOccurrenceId id;

  private LocalDateTime occurrenceEndUtc;

  @Embeddable
  @EqualsAndHashCode
  public static class EventOccurrenceId implements Serializable {
    private Long eventId;
    private LocalDateTime occurrenceStartUtc;
    // equals/hashCode

  }
}
