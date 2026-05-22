package model.projects;

import java.time.temporal.Temporal;
import java.util.stream.Stream;

import org.openntf.xsp.jakarta.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ItemStorage;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import util.StringUtil;

@Entity("response")
public class Response {
	@RepositoryProvider("projectsRepository")
	public interface Repository extends DominoRepository<Response, String> {
		@ViewEntries("Discussion2")
		Stream<Response> findTree(ViewQuery query);
	}
	
	@Id
	private String documentId;
	@Column
	private String projectName;
	@Column
	private String subject;
	@Column("BodyR")
	@ItemStorage(type = ItemStorage.Type.MIME)
	private String body;
	@Column("BodyMarkdown")
	private String bodyMarkdown;
	@Column
	private String mainId;
	@Column
	private String responseId;
	@Column(DominoConstants.FIELD_POSITION)
	private String viewPosition;
	@Column("Entry_Person")
	private String author;
	@Column("Entry_Date")
	private Temporal date;
	
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getBodyMarkdown() {
		return bodyMarkdown;
	}
	public void setBodyMarkdown(String bodyMarkdown) {
		this.bodyMarkdown = bodyMarkdown;
	}
	public String getMainId() {
		return mainId;
	}
	public void setMainId(String mainId) {
		this.mainId = mainId;
	}
	public String getResponseId() {
		return responseId;
	}
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
	
	public String getViewPosition() {
		return viewPosition;
	}
	public void setViewPosition(String viewPosition) {
		this.viewPosition = viewPosition;
	}
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Temporal getDate() {
		return date;
	}
	public void setDate(Temporal date) {
		this.date = date;
	}
	
	/**
	 * Determines the "indent" level within a conversation. Top-level entries have
	 * a value of 0, their responses have 1, responses to those have 2, and so forth.
	 * 
	 * @return the conversational indent level of this response
	 */
	public int getIndentLevel() {
		var pos = getViewPosition();
		if(StringUtil.isEmpty(pos)) {
			return 0;
		}
		int count = (int)pos.chars().filter(c -> c == '.').count();
		return Math.max(0, count - 2);
	}
}
