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

import java.util.List;
import java.util.stream.Stream;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;

@Entity("Setting")
public class ConfigEntry {
	public interface Repository extends DominoRepository<ConfigEntry, String> {
		@ViewEntries("Settings")
		Stream<ConfigEntry> findAll();
	}
	
	@Column("Key")
	private String key;
	@Column("Name")
	private String name;
	@Column("Comments")
	private String comments;
	@Column("Value1")
	private List<String> value1;
	@Column("Value2")
	private List<String> value2;
	@Column("Value3")
	private List<String> value3;
	
	public ConfigEntry() {
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public List<String> getValue1() {
		return value1;
	}
	public void setValue1(List<String> value1) {
		this.value1 = value1;
	}
	public List<String> getValue2() {
		return value2;
	}
	public void setValue2(List<String> value2) {
		this.value2 = value2;
	}
	public List<String> getValue3() {
		return value3;
	}
	public void setValue3(List<String> value3) {
		this.value3 = value3;
	}
}
