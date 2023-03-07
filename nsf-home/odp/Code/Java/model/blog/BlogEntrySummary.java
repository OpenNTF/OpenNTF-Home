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

import java.time.OffsetDateTime;
import java.util.Comparator;

public class BlogEntrySummary implements Comparable<BlogEntrySummary> {
	private static final Comparator<BlogEntrySummary> COMPARATOR = Comparator.comparing(BlogEntrySummary::getDate)
            .thenComparing(BlogEntrySummary::getUnid);

	private String title;
	private String unid;
	private OffsetDateTime date;
	
	public BlogEntrySummary(String title, String unid, OffsetDateTime date) {
		this.title = title;
		this.unid = unid;
		this.date = date;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getUnid() {
		return unid;
	}
	public void setUnid(String unid) {
		this.unid = unid;
	}

	public OffsetDateTime getDate() {
		return date;
	}
	public void setDate(OffsetDateTime date) {
		this.date = date;
	}

	@Override
	public int compareTo(BlogEntrySummary o) {
		return COMPARATOR.compare(this, o);
	}
}
