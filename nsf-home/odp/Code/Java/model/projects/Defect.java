/**
 * Copyright (c) 2022-2024 Contributors to the OpenNTF Home App Project
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
import java.util.stream.Stream;

import org.openntf.xsp.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.nosql.mapping.extension.ItemStorage;
import org.openntf.xsp.nosql.mapping.extension.RepositoryProvider;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Sorts;

@Entity("bug")
public class Defect {
	@RepositoryProvider("projectsRepository")
	public interface Repository extends DominoRepository<Defect, String> {
		Stream<Defect> findByProjectName(String projectName, Sorts sorts);
	}
	
	@Column(DominoConstants.FIELD_ID)
	private String documentId;
	@Column("Subject")
	private String subject;
	@Column("Details")
	@ItemStorage(type=ItemStorage.Type.MIME)
	private String body;
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
