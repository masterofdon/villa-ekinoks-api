package com.villaekinoks.app.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeUtils {

	private TimeUtils() {

	}

	public static final int ISTANBUL_TIME_OFFSET = 1;

	public static final String TIME_ZONE = "Etc/GMT-3";

	public static String stampTimeNow() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date now = new Date();
		return dateFormat.format(now);
	}

	public static String stampTime1Day() {
		return stampTimeDaysFromNow(1);
	}

	public static String stampTimeDaysFromNow(int days) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DATE, days); // number of days to add
		return dateFormat.format(calendar.getTime()); // dt is now the new date
	}

	public static Date timeStampDate() {
		return Date.from(tsInstantNow());
	}

	public static LocalTime timeFromNow(int minutes) {
		return LocalTime.now().plus(minutes, ChronoUnit.MINUTES);
	}

	public static Instant tsInstantNow() {
		return LocalDateTime.now().toInstant(ZoneOffset.ofHours(TimeUtils.ISTANBUL_TIME_OFFSET));
	}

	public static String tsStringNow() {
		return String.valueOf(Instant.now().toEpochMilli());
	}

	public static String tsStringMillisLater(Long millis) {
		return String.valueOf(Instant.now().toEpochMilli() + millis);
	}

	public static TimeZone getCurrentTimeZone() {
		return TimeZone.getTimeZone(TIME_ZONE);
	}

	public static Date getIstanbulDate() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
		return calendar.getTime();

	}

	public static LocalDate getCurrentLocalDate() {
		return LocalDate.now();
	}

	public static LocalDate get30NovemberLocalDate() {
		return LocalDate.of(2018, 11, 30);
	}

	public static LocalDate getSomeDate(int year, int month, int day) {
		return LocalDate.of(year, month, day);
	}

	public static LocalDate getSomeDate(String dateStr, String dateFormat) {
		return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(dateFormat));
	}

	public static String getTodaysString() {
		LocalDate date = LocalDate.now();
		int day = date.getDayOfMonth();
		int month = date.getMonthValue();
		int year = date.getYear();
		LocalDate theDay = TimeUtils.getSomeDate(year, month, day);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return theDay.format(formatter);
	}

	public static String getTodaysString(String format) {
		LocalDate date = LocalDate.now();
		int day = date.getDayOfMonth();
		int month = date.getMonthValue();
		int year = date.getYear();
		LocalDate theDay = TimeUtils.getSomeDate(year, month, day);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return theDay.format(formatter);
	}

	public static String getDayString(LocalDate date, String format) {
		int day = date.getDayOfMonth();
		int month = date.getMonthValue();
		int year = date.getYear();
		LocalDate theDay = TimeUtils.getSomeDate(year, month, day);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return theDay.format(formatter);
	}

	public static Date getIstanbulDateXMinutesNow(int minutes) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	public static boolean is5MinBeforeCurrentTime(Long lastreqtime) {
    if(lastreqtime == null) {
      return false;
    }
		Long currentTime = tsInstantNow().toEpochMilli();
		Long timeDiff = currentTime - lastreqtime;
		Long timeDiffInMin = timeDiff / 1000 / 60;
		return timeDiffInMin <= 5;
	}

	public static List<String> getAllDayDatessBetweenDates(int day, LocalDate startDate, LocalDate endDate) {

		LocalDate currentDate = startDate;
		ArrayList<String> days = new ArrayList<>();
		while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			if (currentDate.getDayOfWeek().getValue() == day) {
				days.add(currentDate.format(formatter));
			}
			currentDate = currentDate.plusDays(1);
		}
		return days;
	}

	public static String getDayMonthInDutchOfLocalDate(LocalDate date) {
		String month = date.getMonth().toString();
		return date.getDayOfMonth() + " " + month;
	}

}
