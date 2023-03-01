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
