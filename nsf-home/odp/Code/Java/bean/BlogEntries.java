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
package bean;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.ibm.commons.util.StringUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.data.page.PageRequest;
import jakarta.ws.rs.NotFoundException;
import model.blog.BlogEntry;

@RequestScoped
public class BlogEntries {
	
	@Inject
	BlogEntry.Repository blogEntryRepository;

	public SortedMap<LocalDate, SortedSet<BlogEntry>> getEntrySummaries() {
		SortedMap<LocalDate, SortedSet<BlogEntry>> result = new TreeMap<>(Comparator.reverseOrder());

		blogEntryRepository.findAll()
			.filter(entry -> StringUtil.isNotEmpty(entry.getUnid()))
			.forEach(entry -> {
				LocalDate midMonth = entry.getOffsetDateTime().toLocalDate().with(ChronoField.DAY_OF_MONTH, 15);
				result.computeIfAbsent(midMonth, d -> new TreeSet<>(Comparator.reverseOrder())).add(entry);
			});

		return result;
	}
	
	public BlogEntry getEntry(String unid) {
		return blogEntryRepository.findById(unid)
			.orElseThrow(() -> new NotFoundException("Unable to find blog entry for ID: " + unid));
	}

	public List<BlogEntry> getEntries(int limit) {
		return blogEntryRepository.findRecent(PageRequest.ofPage(1).size(limit))
			.collect(Collectors.toList());
	}
}
