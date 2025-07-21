package rest.ext;

import java.text.MessageFormat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ext.ParamConverter;
import model.projects.ProjectRelease;

@ApplicationScoped
public class ProjectReleaseParamConverter implements ParamConverter<ProjectRelease> {
	@Inject
	private ProjectRelease.Repository releaseRepository;
	
	@Override
	public ProjectRelease fromString(String value) {
		return releaseRepository.findById(value)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Release for ID {0}", value)));
	}

	@Override
	public String toString(ProjectRelease value) {
		return value.getDocumentId();
	}

}
