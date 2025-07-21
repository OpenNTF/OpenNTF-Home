package controller.projects;

import com.ibm.commons.util.StringUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import model.projects.Project;

/**
 * This bean produces projects for REST endpoints that contain a
 * project name path parameter.
 */
@RequestScoped
public class ContextProjectBean {
	@Inject
	private UriInfo uriInfo;
	
	@Inject
	private Project.Repository projectRepository;
	
	@Produces @UriParameter("")
	public Project produceContextProject(InjectionPoint point) {
		if(uriInfo != null) {
			String param = point.getAnnotated().getAnnotation(UriParameter.class).value();
			if(StringUtil.isEmpty(param)) {
				throw new IllegalStateException("UriParameter.value cannot be empty");
			}
			
			if(uriInfo.getPathParameters().containsKey(param)) {
				String key = uriInfo.getPathParameters().getFirst(param).replace('+', ' ');
				return projectRepository.findByProjectName(key)
					.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
			}
			return null;
		}
		return null;
	}
}
