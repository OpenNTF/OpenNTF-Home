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
package model.home;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.openntf.xsp.jakarta.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity("company")
public class AllianceMember {

	@RepositoryProvider("homeRepository")
	public interface Repository extends DominoRepository<AllianceMember, String> {
		@ViewEntries("AM")
		Stream<AllianceMember> getActiveMembers();
		
		@ViewEntries("AM")
		Optional<AllianceMember> findActiveMemberByName(ViewQuery query);
	}
	
	@Id
	private String documentId;
	
	@Column("Name")
	private String name;
	
	@Column
	private String website;
	
	@Column("Content")
	private String description;
	
	@Column
	private String status;
	
	@Column(DominoConstants.FIELD_ATTACHMENTS)
	private List<EntityAttachment> attachments;
	
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public List<EntityAttachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<EntityAttachment> attachments) {
		this.attachments = attachments;
	}
}
