package controller.projects;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import model.projects.Project;

/**
 * This bean produces projects for REST endpoints that contain a
 * "projectName" path parameter.
 */
@RequestScoped
public class ContextProjectBean {
	@Inject
	private UriInfo uriInfo;
	
	@Inject
	private Project.Repository projectRepository;
	
	@Produces @UriParameter
	public Project produceContextProject() {
		if(uriInfo != null) {
			if(uriInfo.getPathParameters().containsKey("projectName")) {
				String key = uriInfo.getPathParameters().getFirst("projectName").replace('+', ' ');
				return projectRepository.findByProjectName(key)
					.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
			}
			return null;
		}
		return null;
	}
}
