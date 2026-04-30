package controller.projects;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.eclipse.krazo.engine.Viewable;

import bean.UserInfoBean;
import controller.ControllerUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.RuntimeDelegate;
import model.projects.Project;
import model.projects.ProjectRelease;
import rest.ext.ValidProjectRelationship;
import util.StringUtil;

@Path("projects/{project}/releases")
@ValidProjectRelationship
public class ReleasesController {
	
	@Inject
	private Models models;
	
	@Inject
	private ProjectRelease.Repository projectReleaseRepository;

    @Context
    private Request request;
    
    @Inject
    private UserInfoBean userInfo;

    @PathParam("project")
    private Project project;
    
    @PathParam("release")
    private ProjectRelease release;
    
    @Inject
    private ControllerUtil controllerUtil;
    
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String showReleases() {
		models.put("project", project);
		
		models.put("projectEditable", controllerUtil.isProjectEditable(project));
		
		return "project/releases.jsp";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProjectRelease> getReleasesJson() {
		return project.getReleasesByDate();
	}
	
	@Path("{release}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response showRelease() {
		boolean anon = userInfo.isAnonymous();
		EntityTag etag = null;
		if(anon) {
			etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(release.getEtag());
			ResponseBuilder response = request.evaluatePreconditions(etag);
			if(response != null) {
				return response.build();
			}
		}
		
		models.put("project", project);
		
		models.put("release", release);
		
		models.put("projectEditable", controllerUtil.isProjectEditable(project));
		models.put("releaseEditable", controllerUtil.isEditable(release));

		if(anon) {
			return Response.ok(new Viewable("project/releases.jsp"))
				.header(HttpHeaders.ETAG,  etag.getValue())
				.build();
		} else {
			return Response.ok(new Viewable("project/releases.jsp"))
				.build();
		}
	}
	
	@Path("{release}/@edit")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/release-edit.jsp")
	@RolesAllowed("login")
	public void editProjectRelease() {
		if(!controllerUtil.isProjectEditable(project)) {
			throw new NotAuthorizedException("Project is not editable", Response.status(Status.UNAUTHORIZED).build());
		}
		if(!controllerUtil.isEditable(release)) {
			throw new NotAuthorizedException("You are not authorized to edit this release", Response.status(Status.UNAUTHORIZED).build());
		}
		
		models.put("project", project);
		
		models.put("release", release);
	}
	
	@Path("@new")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/release-edit.jsp")
	@RolesAllowed("login")
	public void composeProjectRelease() {
		if(!controllerUtil.isProjectEditable(project)) {
			throw new NotAuthorizedException("Project is not editable", Response.status(Status.UNAUTHORIZED).build());
		}
		
		models.put("project", project);
		
		var release = new ProjectRelease();
		release.setAttachments(new ArrayList<>());
		models.put("release", release);
	}
	
	@Path("@new")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Controller
	@RolesAllowed("login")
	public String createProjectRelease(List<EntityPart> entityParts) {
		if(!controllerUtil.isProjectEditable(project)) {
			throw new NotAuthorizedException("Project is not editable", Response.status(Status.UNAUTHORIZED).build());
		}
		
		var release = updateReleaseFromPayload(new ProjectRelease(), entityParts);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/releases/" + release.getDocumentId();
	}
	
	@Path("{release}")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Controller
	@RolesAllowed("login")
	public String updateProjectRelease(List<EntityPart> entityParts) {
		if(!controllerUtil.isProjectEditable(project)) {
			throw new NotAuthorizedException("Project is not editable", Response.status(Status.UNAUTHORIZED).build());
		}
		
		if(!controllerUtil.isEditable(release)) {
			throw new NotAuthorizedException("Release is not editable", Response.status(Status.UNAUTHORIZED).build());
		}
		
		var release = updateReleaseFromPayload(this.release, entityParts);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/releases/" + release.getDocumentId();
	}
	
	@Path("{release}")
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Controller
	@RolesAllowed("login")
	public String deleteProjectRelease() {
		if(!controllerUtil.isProjectEditable(project)) {
			throw new NotAuthorizedException("Project is not editable", Response.status(Status.UNAUTHORIZED).build());
		}
		
		if(!controllerUtil.isEditable(release)) {
			throw new NotAuthorizedException("Release is not editable", Response.status(Status.UNAUTHORIZED).build());
		}
		
		projectReleaseRepository.delete(release);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/releases";
	}
	
	@Path("{release}/{fileName}")
	@GET
	public Response getProjectReleaseFile(@PathParam("fileName") String fileName) throws IOException {
		return controllerUtil.fetchAttachment(release, fileName, request);
	}
	
	private ProjectRelease updateReleaseFromPayload(ProjectRelease release, List<EntityPart> entityParts) {
		String releaseVersion = null;
		String releaseLicense = null;
		boolean releaseReleased = false;
		String releaseDescription = null;
		Set<String> deleteAttachments = new HashSet<>();
		List<EntityAttachment> attachments = new ArrayList<>();
		for(EntityPart part : entityParts) {
			switch(String.valueOf(part.getName())) {
			case "releaseVersion" -> releaseVersion = controllerUtil.toString(part);
			case "releaseLicense" -> releaseLicense = controllerUtil.toString(part);
			case "releaseReleased" -> releaseReleased = "true".equals(controllerUtil.toString(part));
			case "releaseDescription" -> releaseDescription = controllerUtil.toString(part);
			case "deleteAttachments" -> deleteAttachments.add(controllerUtil.toString(part));
			case "releaseFiles" -> {
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
		
		release.setVersion(releaseVersion);
		release.setLicenseType(releaseLicense);
		release.setReleased(releaseReleased);
		release.setDescription(releaseDescription);
		
		release.setProjectName(project.getName());
		
		if(StringUtil.isEmpty(release.getDocumentId())) {
			// Then it's new - configure some defaults
			release.setReleaseDate(LocalDate.now());
			release.setMasterChef(List.of(userInfo.getDisplayName()));
		}
		
		var existingAttachments = release.getAttachments();
		if(existingAttachments != null) {
			existingAttachments.stream()
				.filter(att -> !deleteAttachments.contains(att.getName()))
				.forEach(attachments::add);
		}
		release.setAttachments(attachments);
		
		return projectReleaseRepository.save(release, true);
	}
}
