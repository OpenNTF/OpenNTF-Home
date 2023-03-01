package webapp.resources.iptools;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import model.projects.ProjectRelease;

@Path("iptools/projects")
@RolesAllowed("[IPManager]")
public class IPProjectsResource {
	
	@Inject
	private ProjectRelease.Repository projectReleases;
	
	@GET
	@Path("pendingReleases")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getPendingReleases() {
		return Collections.singletonMap("payload", projectReleases.findPendingReleases().collect(Collectors.toList()));
	}
	
	@GET
	@Path("releases")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProjectRelease> getReleases(@QueryParam("daysAgo") int daysAgo) {
		// TODO implement "days ago"
		return Collections.emptyList();
	}
	
	@POST
	@Path("releases/{documentId}/approve")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean approveRelease(@PathParam("documentId") @NotEmpty String documentId) {
		ProjectRelease release = projectReleases.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Could not find project for UNID {0}", documentId)));
		release.markApprovedForCatalog(true);
		projectReleases.save(release);
		
		return true;
	}
}
