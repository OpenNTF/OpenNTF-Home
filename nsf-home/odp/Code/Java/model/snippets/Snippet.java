package model.snippets;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import bean.ApplicationConfig;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public class Snippet {
	@RepositoryProvider("snippetsRepository")
	public interface Repository extends DominoRepository<Snippet, String> {
		@ViewEntries("SnippetsByDate")
		public Stream<Snippet> findRecent(PageRequest pagination);
		
		@ViewEntries("SnippetsByAuthor")
		public Stream<Snippet> findByAuthor(ViewQuery viewQuery);
	}
	
	@Id
	private String documentId;
	@Column("Id")
	private String snippetId;
	@Column("Language")
	private String language;
	@Column("Name")
	private String name;
	@Column("Notes")
	private String notes;
	@Column("datecreated")
	private OffsetDateTime creationDate;
	@Column("Author")
	private String author;
	@Column("Body")
	private String body;
	@Column("Rating")
	private Integer rating;
	@Column("RatingRateCount")
	private Integer ratingCount;
	
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getSnippetId() {
		return snippetId;
	}
	public void setSnippetId(String snippetId) {
		this.snippetId = snippetId;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public OffsetDateTime getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(OffsetDateTime creationDate) {
		this.creationDate = creationDate;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	public Integer getRatingCount() {
		return ratingCount;
	}
	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}
	
	public String getLink() {
		ApplicationConfig config = CDI.current().select(ApplicationConfig.class).get();
		String format = config.getSnippetUrlFormat();
		String encodedId = URLEncoder.encode(getSnippetId(), StandardCharsets.UTF_8);
		return MessageFormat.format(format, encodedId);
	}
}
