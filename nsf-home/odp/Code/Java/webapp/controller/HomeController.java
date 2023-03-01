package webapp.controller;

import bean.BlogEntries;
import bean.ProjectReleases;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@Controller
public class HomeController {

	@Inject
	Models models;
	
	@Inject
	ProjectReleases projectReleases;
	
	@Inject
	BlogEntries blogEntries;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String get() {
		models.put("recentReleases", projectReleases.getRecentReleases(30)); //$NON-NLS-1$
		models.put("blogEntries", blogEntries.getEntries(5)); //$NON-NLS-1$
		
		return "home.jsp"; //$NON-NLS-1$
	}

}
