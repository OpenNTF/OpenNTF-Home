package webapp.controller;

import bean.BlogEntries;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/blog")
@Controller
public class BlogController {

	@Inject
	Models models;
	
	@Inject
	BlogEntries blogEntries;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String get() {
		models.put("blogEntrySummaries", blogEntries.getEntrySummaries()); //$NON-NLS-1$
		
		return "blog.jsp"; //$NON-NLS-1$
	}
	
	@Path("{entryUnid}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getEntry(@PathParam("entryUnid") String entryUnid) {
		models.put("entry", blogEntries.getEntry(entryUnid));
		
		return "blogEntry.jsp";
	}
	
}