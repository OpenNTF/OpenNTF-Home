package model.blog;

import java.time.OffsetDateTime;
import java.util.Comparator;

public class BlogEntrySummary implements Comparable<BlogEntrySummary> {
	private static final Comparator<BlogEntrySummary> COMPARATOR = Comparator.comparing(BlogEntrySummary::getDate)
            .thenComparing(BlogEntrySummary::getUnid);

	private String title;
	private String unid;
	private OffsetDateTime date;
	
	public BlogEntrySummary(String title, String unid, OffsetDateTime date) {
		this.title = title;
		this.unid = unid;
		this.date = date;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getUnid() {
		return unid;
	}
	public void setUnid(String unid) {
		this.unid = unid;
	}

	public OffsetDateTime getDate() {
		return date;
	}
	public void setDate(OffsetDateTime date) {
		this.date = date;
	}

	@Override
	public int compareTo(BlogEntrySummary o) {
		return COMPARATOR.compare(this, o);
	}
}
