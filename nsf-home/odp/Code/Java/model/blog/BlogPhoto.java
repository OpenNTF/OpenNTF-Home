/**
 * Copyright (c) 2022-2023 Contributors to the OpenNTF Home App Project
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
package model.blog;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.openntf.xsp.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.nosql.mapping.extension.RepositoryProvider;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;
import model.AbstractAttachmentEntity;

@Entity("content_Photo")
public class BlogPhoto extends AbstractAttachmentEntity {
	@RepositoryProvider("blogRepository")
	public interface Repository extends DominoRepository<BlogPhoto, String> {
		Stream<BlogPhoto> findAll();
		
		Optional<BlogPhoto> findByTitle(String title);
	}

	@Id
	private String documentId;
	
	@Column(DominoConstants.FIELD_ATTACHMENTS)
	private List<EntityAttachment> attachments;
	@Column(DominoConstants.FIELD_REPLICAID)
	private String replicaId;
	@Column("PhotoAlbum")
	private String album;
	@Column("PhotoAuthor")
	private String author;
	@Column("PhotoDate")
	private Temporal photoDate;
	@Column("PhotoStatus")
	private BlogStatus status;
	@Column(DominoConstants.FIELD_MDATE)
	private Instant modified;
	@Column(DominoConstants.FIELD_CDATE)
	private Instant created;
	@Column("PhotoTitle")
	private String title;
	
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
	
	public BlogStatus getStatus() {
		return status == null ? BlogStatus.Draft : status;
	}
	public void setStatus(BlogStatus status) {
		this.status = status;
	}
	
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Temporal getPhotoDate() {
		return photoDate;
	}
	public void setPhotoDate(Temporal photoDate) {
		this.photoDate = photoDate;
	}
	
	public Instant getModified() {
		return modified;
	}
	public void setModified(Instant modified) {
		this.modified = modified;
	}
	
	public Instant getCreated() {
		return created;
	}
	public void setCreated(Instant created) {
		this.created = created;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
