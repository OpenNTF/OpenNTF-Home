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
package model.home;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity("Page")
public class Page {
	@RepositoryProvider("homeRepository")
	public interface Repository extends DominoRepository<Page, String> {
		Optional<Page> findBySubject(String subject);
	}
	
	@Id
	private String id;
	@Column("PermLink")
	private String linkId;
	@Column("Subject")
	private String title;
	@Column("Author")
	private String author;
	@Column("creation")
	private OffsetDateTime created;
	@Column("content")
	private String html;
	@Column("childpages")
	private List<String> childPageIds;
	
	public Page() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getLinkId() {
		return linkId;
	}
	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public List<String> getChildPageIds() {
		return childPageIds;
	}

	public void setChildPageIds(List<String> childPageIds) {
		this.childPageIds = childPageIds;
	}
	
	public Set<String> getCleanChildPageIds() {
		Set<String> childPageIds = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		childPageIds.addAll(getChildPageIds());
		childPageIds.remove(getId());
		return childPageIds;
	}
}
