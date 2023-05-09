package model.webinars;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import org.openntf.xsp.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.nosql.mapping.extension.ViewEntries;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

@Entity("ninWebinar")
public class Webinar {
	@RepositoryProvider("webinarsRepository")
	public interface Repository extends DominoRepository<Webinar, String> {
		@ViewEntries("webinarsPublic")
		Stream<Webinar> getPublicWebinars();
	}
	
	@Id
	private String documentId;
	@Column
	private Instant date;
	@Column
	private String topic;
	@Column("presenters")
	private List<String> presenterIds;
	
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public Instant getDate() {
		return date;
	}
	public void setDate(Instant date) {
		this.date = date;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public List<String> getPresenterIds() {
		return presenterIds;
	}
	public void setPresenterIds(List<String> presenterIds) {
		this.presenterIds = presenterIds;
	}
	
}
