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
package model.ct;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import com.ibm.commons.util.StringUtil;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import jakarta.data.page.PageRequest;
import jakarta.data.Sort;

@Entity
public class CtEntry {
	@RepositoryProvider("ctRepository")
	public interface Repository extends DominoRepository<CtEntry, String> {
		@ViewEntries("NewsAll")
		Stream<CtEntry> listEntries(Sort<CtEntry> sorts, PageRequest pagination);
	}
	
	@Id
	private String id;
	@Column("NState")
	private String state;
	@Column("NCreationDate")
	private OffsetDateTime creationDate;
	@Column("NTitle")
	private String title;
	@Column("NLink")
	private String link;
	@Column("PID")
	private String pid;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public OffsetDateTime getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(OffsetDateTime creationDate) {
		this.creationDate = creationDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	
	public String getPersonDisplayName() {
		String pid = getPid();
		if(StringUtil.isEmpty(pid)) {
			return "";
		}
		
		CtPerson.Repository repository = CDI.current().select(CtPerson.Repository.class).get();
		return repository.findByPid(ViewQuery.query().key(pid, true))
			.map(CtPerson::getDisplayName)
			.orElse("");
	}
}
