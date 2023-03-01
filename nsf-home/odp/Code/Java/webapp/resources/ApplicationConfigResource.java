package webapp.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import bean.ApplicationConfig;

@Path("/config")
public class ApplicationConfigResource {
	
	@Inject
	ApplicationConfig config;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ApplicationConfig get() {
		return config;
	}

}