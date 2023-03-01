package bean;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;

import org.ocpsoft.prettytime.PrettyTime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
@Named("temporalBean")
public class TemporalBean {
	private PrettyTime prettyTime = new PrettyTime();
	private static DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");

	@Inject
	HttpServletRequest request;

	@Produces
	public PrettyTime getPrettyTime() {
		return prettyTime;
	}

	public String timeAgo(TemporalAccessor time) {
		if(time == null) {
			return "";
		}
		Instant inst;
		try {
			inst = Instant.from(time);
		} catch (DateTimeException e) {
			// Then it's just a date
			LocalDate localDate = LocalDate.from(time);
			ZonedDateTime dt = ZonedDateTime.of(localDate, LocalTime.of(12, 0), ZoneId.systemDefault());
			inst = dt.toInstant();
		}
		return prettyTime.format(inst);
	}

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
