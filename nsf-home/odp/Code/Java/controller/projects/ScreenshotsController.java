package controller.projects;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

import controller.ControllerUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import model.projects.Project;
import model.projects.Screenshot;
import rest.ext.ValidProjectRelationship;
import util.StringUtil;

@Path("projects/{project}/screenshots")
@ValidProjectRelationship
public class ScreenshotsController {
	
	@Inject
	private Models models;
	
	@Inject
	private Screenshot.Repository screenshotRepository;
	
	@Inject
	private ControllerUtil controllerUtil;

    @Context
    private Request request;
    
    @PathParam("project")
    private Project project;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectScreenshots() {
		models.put("project", project);
		
		return "project/screenshots.jsp";
	}
	
	@Path("{screenshot}/{fileName}")
	@GET
	public Response getProjectScreenshot(@PathParam("screenshot") Screenshot screenshot, @PathParam("fileName") String fileName) throws IOException {
		return controllerUtil.fetchAttachment(screenshot, fileName, request);
	}
	
	@Path("@new")
	@GET
	@Controller
	@View("project/screenshot-edit.jsp")
	@RolesAllowed("login")
	public void composeScreenshot() {
		controllerUtil.validateEditable(project);
		
		models.put("project", project);
		
		var screenshot = new Screenshot();
		screenshot.setAttachments(new ArrayList<>());
		models.put("screenshot", screenshot);
	}

	@Path("@new")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Controller
	@RolesAllowed("login")
	public String createScreenshot(List<EntityPart> entityParts) {
		controllerUtil.validateEditable(project);
		
		String description = null;
		List<EntityAttachment> attachments = new ArrayList<>();
		for(EntityPart part : entityParts) {
			switch(String.valueOf(part.getName())) {
			case "description" -> description = controllerUtil.toString(part);
			case "files" -> {
				String fileName = part.getFileName()
					.map(name -> StringUtil.isEmpty(name) ? UUID.randomUUID().toString() : name)
					.orElseGet(() -> UUID.randomUUID().toString());
				String type = part.getMediaType().toString();
				byte[] data;
				try {
					data = part.getContent(byte[].class);
				} catch (IllegalArgumentException | IllegalStateException | WebApplicationException | IOException e) {
					throw new RuntimeException(e);
				}
				
				if(data.length > 0) {
					attachments.add(EntityAttachment.of(fileName, System.currentTimeMillis(), type, data));
				}
			}
			}
		}
		
		var screenshot = new Screenshot();
		screenshot.setProjectName(project.getName());
		screenshot.setAttachments(attachments);
		screenshot.setDescription(description);
		
		if(StringUtil.isEmpty(screenshot.getDocumentId())) {
			screenshot.setDate(OffsetDateTime.now());
		}
		
		screenshotRepository.save(screenshot, true);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/screenshots";
	}
	
	@Path("{screenshot}/{fileName}")
	@DELETE
	@Controller
	public String deleteProjectScreenshot(@PathParam("screenshot") Screenshot screenshot, @PathParam("screenshotId") String screenshotId, @PathParam("fileName") String fileName) throws IOException {
		controllerUtil.validateEditable(project, screenshot);
		
		List<EntityAttachment> newAttachments = screenshot.getAttachments()
			.stream()
			.filter(att -> !fileName.equals(att.getName()))
			.toList();
		if(newAttachments.isEmpty()) {
			screenshotRepository.delete(screenshot);
		} else {
			screenshot.setAttachments(newAttachments);
			screenshotRepository.save(screenshot, true);
		}

		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/screenshots";
	}
}
