/**
 * Copyright (c) 2022-2025 Contributors to the OpenNTF Home App Project
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
package model.projects;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.openntf.xsp.jakarta.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ItemStorage;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewDocuments;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import model.AbstractAttachmentEntity;

@Entity("feature")
public class FeatureRequest extends AbstractAttachmentEntity implements ProjectRelative {
	public static final String VIEW_ALL = "FeatureRequests";
	public static final String VIEW_ADDED = "Project\\Feature Requests-Added";
	public static final String VIEW_INVESTIGATING = "Project\\Feature Requests-Investigating";
	public static final String VIEW_REJECTED = "Project\\Feature Requests-Rejected";
	public static final String VIEW_SUBMITTED = "Project\\Feature Requests-Submitted";
	
	@RepositoryProvider("projectsRepository")
	public interface Repository extends DominoRepository<FeatureRequest, String> {
		@ViewDocuments(value=VIEW_ALL, maxLevel = 2)
		Stream<FeatureRequest> listAll(ViewQuery query);
	}
	
	@Id
	private String documentId;
	@Column(DominoConstants.FIELD_ATTACHMENTS)
	private List<EntityAttachment> attachments;
	@Column(DominoConstants.FIELD_REPLICAID)
	private String replicaId;
	@Column("Description")
	private String description;
	@Column("Details")
	@ItemStorage(type=ItemStorage.Type.MIME)
	private String body;
	@Column("DetailsMarkdown")
	private String bodyMarkdown;
	@Column("Entry_Date")
	private Temporal entryDate;
	@Column("Entry_Person")
	private String entryAuthor;
	@Column("ProjectName")
	private String projectName;
	@Column(DominoConstants.FIELD_ETAG)
	private String etag;
	
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
	public void setSubject(String description) {
		this.description = description;
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
	
	@Override
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
