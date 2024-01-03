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

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.nosql.mapping.Pagination;
import model.projects.ProjectRelease;

@RequestScoped
@Named("Projects")
public class ProjectReleases {
	
	@Inject
	private ProjectRelease.Repository projectReleaseRepository;

	public List<ProjectRelease> getRecentReleases(int limit) {
		Pagination pagination = Pagination.page(1).size(limit);
		return projectReleaseRepository.findRecent(pagination).collect(Collectors.toList());
	}
}
