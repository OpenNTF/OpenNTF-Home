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
package model.projects;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import org.openntf.xsp.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.nosql.mapping.extension.ViewQuery;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;

// NB: this entity represents many types of forms in the database
//     and should not be created or saved
@Entity("ProjectActivity")
public class ProjectActivity {
	@RepositoryProvider("projectsRepository")
	public interface Repository extends DominoRepository<ProjectActivity, String> {
		public static final String VIEW_ALLBYDATE = "AllbyDate";
		
		@ViewEntries(VIEW_ALLBYDATE)
		Stream<ProjectActivity> findByDate(ViewQuery viewQuery);
	}

	@Column(DominoConstants.FIELD_ID)
	private String documentId;
	@Column("$10")
	private String projectName;
	@Column("Entry_Date")
	private OffsetDateTime date;
	@Column("$12")
	private String createdBy;
	@Column("Form")
	private String form;
	@Column("subject")
	private String subject;
	
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
	public OffsetDateTime getDate() {
		return date;
	}
	public void setDate(OffsetDateTime date) {
		this.date = date;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
