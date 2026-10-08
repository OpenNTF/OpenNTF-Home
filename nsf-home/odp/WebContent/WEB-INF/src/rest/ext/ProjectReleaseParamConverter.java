package rest.ext;

import java.text.MessageFormat;

import controller.ControllerUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Models;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ext.ParamConverter;
import model.projects.ProjectRelease;

@ApplicationScoped
public class ProjectReleaseParamConverter implements ParamConverter<ProjectRelease> {
	@Inject
	private ProjectRelease.Repository releaseRepository;
	
	@Inject
	private Models models;
	
	@Inject
	private ControllerUtil controllerUtil;
	
	@Override
	public ProjectRelease fromString(String value) {
		ProjectRelease release = releaseRepository.findById(value)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Release for ID {0}", value)));
		models.put("docEditable", controllerUtil.isEditable(release));
		return release;
	}

	@Override
	public String toString(ProjectRelease value) {
		return value.getDocumentId();
	}

}
