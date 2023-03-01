package model.projects;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.openntf.xsp.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.nosql.mapping.extension.RepositoryProvider;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;
import model.AbstractAttachmentEntity;

@Entity("documentation")
public class Documentation extends AbstractAttachmentEntity {
	@RepositoryProvider("projectsRepository")
	public static interface Repository extends DominoRepository<Documentation, String> {
		Stream<Documentation> findByProjectName(String projectName);
	}
	
	@Id
	private String documentId;
	@Column(DominoConstants.FIELD_ATTACHMENTS)
	private List<EntityAttachment> attachments;
	@Column(DominoConstants.FIELD_REPLICAID)
	private String replicaId;
	@Column
	private String projectName;
	@Column
	private String description;
	@Column("Entry_Date")
	private Temporal entryDate;
	@Column("Entry_Person")
	private String entryAuthor;
	@Column(DominoConstants.FIELD_ETAG)
	private String etag;

	@Override
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	@Override
	public List<EntityAttachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<EntityAttachment> attachments) {
		this.attachments = attachments;
	}

	@Override
	public String getReplicaId() {
		return replicaId;
	}
	public void setReplicaId(String replicaId) {
		this.replicaId = replicaId;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Temporal getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Temporal entryDate) {
		this.entryDate = entryDate;
	}
	
	public String getEntryAuthor() {
		return entryAuthor;
	}
	public void setEntryAuthor(String entryAuthor) {
		this.entryAuthor = entryAuthor;
	}

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getEtag() {
		return etag;
	}
	public void setEtag(String etag) {
		this.etag = etag;
	}
}
