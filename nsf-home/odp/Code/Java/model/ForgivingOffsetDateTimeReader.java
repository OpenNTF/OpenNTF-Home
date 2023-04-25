package model;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import jakarta.annotation.Priority;
import jakarta.nosql.ValueReader;

/**
 * This {@link ValueReader} overrides the default {@link OffsetDateTime} reader
 * to allow for an odd idiom in the projects database where {@code "<none>"} is
 * used in an item that is otherwise a date/time item.
 * 
 * @author Jesse Gallagher
 */
@Priority(1)
public class ForgivingOffsetDateTimeReader implements ValueReader {

    @Override
    public boolean test(Class<?> type) {
        return OffsetDateTime.class.equals(type);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T read(Class<T> typeClass, Object value) {
        return (T) getOffSetDateTime(value);
    }

    private OffsetDateTime getOffSetDateTime(Object value) {
    	if("<none>".equals(value)) {
    		return null;
    	}
    	
        if (OffsetDateTime.class.isInstance(value)) {
            return OffsetDateTime.class.cast(value);
        }

        if (Calendar.class.isInstance(value)) {
            return ((Calendar) value).toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        }

        if (Date.class.isInstance(value)) {
            return ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        }

        if (Number.class.isInstance(value)) {
            return new Date(((Number) value).longValue()).toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        }

        return OffsetDateTime.parse(value.toString());
    }
}
