package controller;

import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.webinars.Webinar;

@Path("webinars")
public class WebinarsController {
	
	@Inject
	private Webinar.Repository webinarsRepository;
	
	@Inject
	private Models models;
	
	@GET
	@Controller
	@Produces(MediaType.TEXT_HTML)
	public String getWebinarsPage() {
		models.put("webinars", webinarsRepository.getPublicWebinars().collect(Collectors.toList()));
		
		return "webinars.jsp";
	}
}
