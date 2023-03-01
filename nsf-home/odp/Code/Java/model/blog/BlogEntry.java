package model.blog;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.openntf.xsp.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.nosql.mapping.extension.ViewDocuments;
import org.openntf.xsp.nosql.mapping.extension.ViewEntries;

import com.ibm.commons.util.StringUtil;

import bean.MarkdownBean;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;
import jakarta.nosql.mapping.Pagination;

@Entity("content_BlogEntry")
public class BlogEntry implements Comparable<BlogEntry> {
	@RepositoryProvider("blogRepository")
	public interface Repository extends DominoRepository<BlogEntry, String> {
		public static final String VIEW_BLOGS = "vw_Content_Blogs"; //$NON-NLS-1$
		public static final String VIEW_BYCATEGORY = "vw_Content_BlogsCategory";
		
		@ViewDocuments(value=VIEW_BLOGS, maxLevel=0)
		Stream<BlogEntry> findRecent(Pagination pagination);
		
		@ViewEntries(value=VIEW_BLOGS, maxLevel=0)
		Stream<BlogEntry> findAll();
		
		@ViewEntries(value=VIEW_BYCATEGORY, maxLevel=0)
		Stream<BlogEntry> listCategories();
	}
	
	private static final Comparator<BlogEntry> COMPARATOR = Comparator.comparing(BlogEntry::getOffsetDateTime).thenComparing(BlogEntry::getUnid);
	
	public enum Type {
		Plain, Rich
	}
	
	@Id
	private String unid;
	
	@Column("EntryTitle")
	private String title;
	@Column("EntryType")
	private Type type;
	@Column("EntryRICH")
	private String bodyRich;
	@Column("EntryHTML")
	private String bodyHtml;
	@Column("EntryMoreHTML")
	private String bodyMoreHtml;
	@Column("EntryMarkdown")
	private String bodyMarkdown;
	@Column("EntryDate")
	private Temporal date;
	@Column("EntryAuthor")
	private String author;
	@Column("$51")
	private String viewTitle;
	@Column(DominoConstants.FIELD_MDATE)
	private Instant modified;
	@Column("EntryCategory")
	private List<String> categories;
	@Column("EntryStatus")
	private BlogStatus status;
	
	public BlogEntry() {
		
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getHtml() {
		if(getType() == Type.Rich) {
			return bodyRich;
		} else {
			if(StringUtil.isNotEmpty(bodyMarkdown)) {
				MarkdownBean markdown = CDI.current().select(MarkdownBean.class).get();
				return markdown.toHtml(bodyMarkdown);
			} else {
				return StringUtil.toString(bodyHtml) + StringUtil.toString(bodyMoreHtml);
			}
		}
	}

	public Temporal getDate() {
		return date;
	}

	public void setDate(Temporal date) {
		this.date = date;
	}
	
	public OffsetDateTime getOffsetDateTime() {
		Temporal date = getDate();
		if(date instanceof LocalDate) {
			return OffsetDateTime.of((LocalDate)date, LocalTime.NOON, ZoneOffset.ofHours(0));
		} else if(date instanceof OffsetDateTime) {
			return (OffsetDateTime)date;
		} else if(date instanceof Instant) {
			return OffsetDateTime.ofInstant((Instant)date, ZoneId.systemDefault());
		} else {
			return OffsetDateTime.MIN;
		}
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getViewTitle() {
		return viewTitle;
	}
	public void setViewTitle(String viewTitle) {
		this.viewTitle = viewTitle;
	}
	
	public Instant getModified() {
		return modified;
	}
	public void setModified(Instant modified) {
		this.modified = modified;
	}
	
	public String getBodyMarkdown() {
		return bodyMarkdown;
	}
	public void setBodyMarkdown(String bodyMarkdown) {
		this.bodyMarkdown = bodyMarkdown;
	}
	
	public List<String> getCategories() {
		return categories == null ? Collections.emptyList() : categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	public BlogStatus getStatus() {
		return status == null ? BlogStatus.Draft : status;
	}
	public void setStatus(BlogStatus status) {
		this.status = status;
	}
	

	@Override
	public int compareTo(BlogEntry o) {
		return COMPARATOR.compare(this, o);
	}
}
