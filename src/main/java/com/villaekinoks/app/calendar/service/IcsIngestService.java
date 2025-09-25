package com.villaekinoks.app.calendar.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.villaekinoks.app.calendar.CalendarEvent;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.RDate;

@Service
@RequiredArgsConstructor
public class IcsIngestService {

  public List<CalendarEvent> ingestVEvent(String icsText) throws IOException, ParserException {

    if (icsText == null || icsText.isBlank()) {
      throw new IllegalArgumentException("ICS text is empty");
    }
    if (!icsText.contains("BEGIN:VCALENDAR") || !icsText.contains("END:VCALENDAR")) {
      throw new IllegalArgumentException("ICS text is not a valid VCALENDAR");
    }

    List<CalendarEvent> results = new ArrayList<>();

    // Ensure CRLF is preserved when serving later
    String normalized = icsText.replace("\r\n", "\n").replace("\n", "\r\n");

    CalendarBuilder builder = new CalendarBuilder();
    try (var in = new ByteArrayInputStream(normalized.getBytes(StandardCharsets.UTF_8))) {
      Calendar cal = builder.build(in);

      // Find first VEVENT (you can loop if multiple)
      List<VEvent> vEvents = cal.getComponents(Component.VEVENT).stream()
          .map(VEvent.class::cast)
          .toList();

      if (vEvents.isEmpty()) {
        return List.of();
      }

      for (VEvent vEvent : vEvents) {
        try {
          String uid = vEvent.getProperty(Property.UID).get().getValue();
          if (uid == null || uid.isBlank()) {
            throw new IllegalArgumentException("No UID found");
          }
          String summary = opt(vEvent, Property.SUMMARY);
          String location = opt(vEvent, Property.LOCATION);
          String tzid = extractTzid(vEvent); // below
          LocalDateTime dtStartUtc = toUtc(vEvent.getDateTimeStart().get());
          LocalDateTime dtEndUtc = toUtc(vEvent.getDateTimeEnd().get());

          String rrule = opt(vEvent, Property.RRULE);
          String exdateJson = toJsonDates(vEvent.getProperties(Property.EXDATE));
          String rdateJson = toJsonDates(vEvent.getProperties(Property.RDATE));
          LocalDateTime lastMod = optDateTime(vEvent, Property.LAST_MODIFIED);
          Integer sequence = optInt(vEvent, Property.SEQUENCE, 0);

          CalendarEvent entity = new CalendarEvent();

          entity.setSummary(summary);
          entity.setLocation(location);
          entity.setTzid(tzid);
          entity.setDtstartUtc(dtStartUtc);
          entity.setDtendUtc(dtEndUtc);
          entity.setRrule(rrule);
          entity.setExdateJson(exdateJson);
          entity.setRdateJson(rdateJson);
          entity.setLastModified(lastMod);
          entity.setSequence(sequence);
          entity.setVersion(entity.getVersion() == null ? 1 : entity.getVersion() + 1);

          results.add(entity);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return results;
  }

  private static String opt(Component comp, String name) {
    var p = comp.getProperty(name).isPresent() ? comp.getProperty(name).get() : null;
    return p != null ? p.getValue() : null;
  }

  private static Integer optInt(Component comp, String name, int def) {
    var p = comp.getProperty(name).isPresent() ? comp.getProperty(name).get() : null;
    return p != null ? Integer.parseInt(p.getValue()) : def;
  }

  private static LocalDateTime optDateTime(Component comp, String name) {
    var p = comp.getProperty(name).isPresent() ? comp.getProperty(name).get() : null;
    if (p instanceof LastModified lm) {
      return lm.getDate().atZone(ZoneOffset.UTC).toLocalDateTime();
    }
    return null;
  }

  private static String extractTzid(VEvent ev) {
    DateProperty<Temporal> sd = ev.getDateTimeStart().get();
    if (sd != null && sd.getParameter(Property.TZID) != null)
      return sd.getParameter(Property.TZID).isPresent() ? sd.getParameter(Property.TZID).get().getValue() : null;
    DateProperty<Temporal> ed = ev.getDateTimeEnd().get();
    if (ed != null && ed.getParameter(Property.TZID) != null)
      return ed.getParameter(Property.TZID).isPresent() ? ed.getParameter(Property.TZID).get().getValue() : null;
    return null;
  }

  private static LocalDateTime toUtc(DateProperty<Temporal> dateProp) {
    if (dateProp == null)
      return null;
    var value = dateProp.getDate();
    ZoneId zone = dateProp.getParameter(Property.TZID).isPresent()
        ? ZoneId.of(dateProp.getParameter(Property.TZID).get().getValue())
        : ZoneId.systemDefault();

    if (value instanceof LocalDateTime dt) {
      return dt.atZone(zone).toLocalDateTime();
    } else if (value instanceof LocalDate d) {
      return d.atStartOfDay(zone).toLocalDateTime();
    }
    return null;
  }

  private static String toJsonDates(List<Property> props) {
    if (props == null || props.isEmpty())
      return null;
    var dates = new ArrayList<String>();
    for (Property p : props) {
      if (p instanceof ExDate ex) {
        ex.getDates().forEach(d -> dates.add(d.toString()));
      } else if (p instanceof RDate rd) {
        rd.getDates().forEach(d -> dates.add(d.toString()));
      }
    }
    return dates.isEmpty() ? null : new Gson().toJson(dates);
  }

  private static String sha1(String s) {
    try {
      var md = MessageDigest.getInstance("SHA-1");
      return HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
}
