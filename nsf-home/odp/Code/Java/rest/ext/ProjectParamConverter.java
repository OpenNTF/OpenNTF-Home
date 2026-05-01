package rest.ext;

import controller.ControllerUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Models;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ext.ParamConverter;
import model.projects.Project;

@ApplicationScoped
public class ProjectParamConverter implements ParamConverter<Project> {
	
	@Inject
	private Project.Repository projectRepository;
	
	@Inject
	private Models models;
	
	@Inject
	private ControllerUtil controllerUtil;

	@Override
	public Project fromString(String value) {
		String key = value.replace('+', ' ');
		Project result = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("projectEditable", controllerUtil.isProjectEditable(result));
		return result;
	}

	@Override
	public String toString(Project value) {
		return value.getName();
	}

}
