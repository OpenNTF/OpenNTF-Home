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
package bean;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.home.ConfigEntry;

@ApplicationScoped
public class ApplicationConfig {
	private Map<String, ConfigEntry> configEntries;
	
	@Inject
	ConfigEntry.Repository configEntryRepository;
	
	@PostConstruct
	public void loadNsfConfig() {
		try {
			this.configEntries = configEntryRepository.findAll()
				.collect(Collectors.toMap(
					ConfigEntry::getKey,
					Function.identity()
				));
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	public Map<String, ConfigEntry> getConfigEntries() {
		return configEntries;
	}
	
	public Optional<ConfigEntry> getConfigEntry(String key) {
		return Optional.ofNullable(configEntries.get(key));
	}
	
	public String getProjectsDbPath() {
		return getSingleValue("dbProjects"); //$NON-NLS-1$
	}
	
	public String getBlogDbPath() {
		return getSingleValue("dbBlog"); //$NON-NLS-1$
	}
	
	public String getHomeDbPath() {
		return getSingleValue("dbHome"); //$NON-NLS-1$
	}
	
	private String getSingleValue(String key) {
		return getConfigEntry(key)
			.map(entry -> entry.getValue1().get(0))
			.orElseThrow(() -> new IllegalStateException(MessageFormat.format("Unable to find setting for {0}", key)));
	}
}
