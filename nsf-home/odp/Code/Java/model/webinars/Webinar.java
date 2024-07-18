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
package model.webinars;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

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
