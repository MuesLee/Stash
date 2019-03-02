package de.ts.stash.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeProvider {

	public static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Berlin");

	public Date convert(final LocalDate date) {
		return Date.from(date.atStartOfDay(TimeProvider.DEFAULT_ZONE).toInstant());
	}

	public Date convert(final LocalDateTime date) {
		return Date.from(date.atZone(DEFAULT_ZONE).toInstant());
	}

	public LocalDate currentDate() {
		return LocalDate.now(DEFAULT_ZONE);
	}

	public LocalDateTime currentDateTime() {
		return LocalDateTime.now(DEFAULT_ZONE);
	}

	public LocalTime currentTime() {
		return LocalTime.now(DEFAULT_ZONE);
	}
}
