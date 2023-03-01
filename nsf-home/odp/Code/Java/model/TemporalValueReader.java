package model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import jakarta.nosql.ValueReader;

public class TemporalValueReader implements ValueReader {

	@Override
	public boolean test(Class<?> c) {
		return Temporal.class.equals(c);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T read(Class<T> clazz, Object value) {
		if(value instanceof Temporal) {
			return (T)value;
		} else if(value == null) {
			return null;
		} else if(value instanceof Date) {
			return (T)((Date)value).toInstant();
		} else {
			String v = value.toString();
			if(v.isEmpty()) {
				return null;
			} else {
				// Try several common formatters
				try {
					TemporalAccessor dt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(v);
					return (T)OffsetDateTime.from(dt);
				} catch(DateTimeParseException e) {
				}
				try {
					TemporalAccessor dt = DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(v);
					return (T)ZonedDateTime.from(dt);
				} catch(DateTimeParseException e) {
				}
				try {
					TemporalAccessor dt = DateTimeFormatter.ISO_LOCAL_DATE.parse(v);
					return (T)LocalDate.from(dt);
				} catch(DateTimeParseException e) {
				}
				try {
					TemporalAccessor dt = DateTimeFormatter.ISO_LOCAL_TIME.parse(v);
					return (T)LocalTime.from(dt);
				} catch(DateTimeParseException e) {
				}
				TemporalAccessor dt = DateTimeFormatter.ISO_INSTANT.parse(v);
				return (T)Instant.from(dt);
			}
		}
	}

}
