/**
 * Copyright (c) 2022-2024 Contributors to the OpenNTF Home App Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jnosql.communication.ValueReader;

import jakarta.annotation.Priority;

/**
 * This {@link ValueReader} overrides the default {@link OffsetDateTime} reader
 * to allow for an odd idiom in the projects database where {@code "<none>"} is
 * used in an item that is otherwise a date/time item.
 * 
 * @author Jesse Gallagher
 */
@Priority(1)
public class ForgivingOffsetDateTimeReader implements ValueReader {
	private static final DateTimeFormatter CT_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

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
    	
    	// Observed in Collaboration Today
    	if(value instanceof String) {
	    	try {
	    		TemporalAccessor temporal = CT_FORMAT.parse((String)value);
	    		LocalDateTime dt = LocalDateTime.from(temporal);
	    		return ZonedDateTime.of(dt, ZoneId.systemDefault()).toOffsetDateTime();
	    	} catch(DateTimeParseException t) {
	    		// Then it doesn't match - continue on
	    	}
    	}

        return OffsetDateTime.parse(value.toString());
    }
}
