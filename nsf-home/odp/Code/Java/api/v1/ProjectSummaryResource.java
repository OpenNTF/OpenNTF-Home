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
package api.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.openntf.xsp.nosql.mapping.extension.ViewQuery;

import jakarta.inject.Inject;
import jakarta.nosql.mapping.Sorts;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.projects.Project;
import model.projects.Screenshot;

@Path("api/v1/projectSummary")
public class ProjectSummaryResource {
	public static class ProjectSummary {
		private String name;
		private String screenshotUrl;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getScreenshotUrl() {
			return screenshotUrl;
		}
		public void setScreenshotUrl(String screenshotUrl) {
			this.screenshotUrl = screenshotUrl;
		}
	}
	
	@Inject
	private Project.Repository projectsRepository;
	
	@Inject
	private Screenshot.Repository screenshotRepository;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProjectSummary> get() {
		return projectsRepository.findAll(null, Sorts.sorts().desc("lastModified"))
			.map(p -> {
				ProjectSummary summary = new ProjectSummary();
				summary.setName(p.getName());
				return summary;
			})
			.peek(summary -> {
				screenshotRepository.findLatestByProjectName(ViewQuery.query().key(summary.getName(), true))
					.filter(screenshot -> !screenshot.getDownloads().isEmpty())
					.map(screenshot -> screenshot.getDownloads().get(0))
					.ifPresent(download -> {
						summary.setScreenshotUrl(download.getUrl());
					});
			})
			.collect(Collectors.toList());
	}
}
