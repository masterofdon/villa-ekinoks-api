package com.villaekinoks.app.calendar.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.villaekinoks.app.calendar.service.IcsIngestService;
import com.villaekinoks.app.generic.api.GenericApiResponse;
import com.villaekinoks.app.generic.api.GenericApiResponseMessages;

import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.ParserException;

@RestController
@RequestMapping("/api/v1/villa-calendars")
@RequiredArgsConstructor
public class VillaCalendarController {

  private final IcsIngestService icsIngestService;

  @GetMapping
  public GenericApiResponse<String> testCalendar() throws IOException, ParserException {  
    
    return new GenericApiResponse<>(
        HttpStatus.OK.value(),
        GenericApiResponseMessages.Generic.SUCCESS, "941851",
        "Villa Calendar Controller is working!");
  }
}
