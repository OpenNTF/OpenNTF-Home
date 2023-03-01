package bean;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
@Named("temporalBean")
public class TemporalBean {
	private static DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");

	@Inject
	HttpServletRequest request;

	public String toMonth(TemporalAccessor time) {
		return MONTH_FORMATTER.withLocale(request.getLocale()).format(time);
	}
	
	public String formatDate(Temporal time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
		if(time instanceof LocalDate) {
			return formatter.format(time);
		} else if(time instanceof OffsetDateTime) {
			return formatter.format(((OffsetDateTime)time).toLocalDate());
		} else {
			Instant inst = Instant.from(time);
			ZonedDateTime dt = ZonedDateTime.ofInstant(inst, ZoneId.systemDefault());
			return formatter.format(dt);
		}
	}

}
