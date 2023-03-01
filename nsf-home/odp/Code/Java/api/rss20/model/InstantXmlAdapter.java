package api.rss20.model;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class InstantXmlAdapter extends XmlAdapter<String, Instant> {

	@Override
	public Instant unmarshal(String v) throws Exception {
		return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(v));
	}

	@Override
	public String marshal(Instant v) throws Exception {
		return DateTimeFormatter.ISO_INSTANT.format(v);
	}

}
