package rest.ext;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ext.ParamConverter;
import model.projects.Project;

@ApplicationScoped
public class ProjectParamConverter implements ParamConverter<Project> {
	
	@Inject
	private Project.Repository projectRepository;

	@Override
	public Project fromString(String value) {
		String key = value.replace('+', ' ');
		return projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
	}

	@Override
	public String toString(Project value) {
		return value.getName();
	}

}
