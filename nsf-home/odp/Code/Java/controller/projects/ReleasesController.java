package controller.projects;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

		if(anon) {
			return Response.ok(new Viewable("project/releases.jsp"))
				.header(HttpHeaders.ETAG,  etag.getValue())
				.build();
		} else {
			return Response.ok(new Viewable("project/releases.jsp"))
				.build();
		}
	}
	
	@Path("@new")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/release-edit.jsp")
	// Users who can edit may not have a specific role in this app, but this is a first check
	@RolesAllowed("login")
	public void composeProjectRelease() {
		boolean projectEditable = controllerUtil.isProjectEditable(project);
		
		if(!projectEditable) {
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
	// Users who can edit may not have a specific role in this app, but this is a first check
	@RolesAllowed("login")
	public String createProjectRelease(
		List<EntityPart> entityParts
//		@NotEmpty @FormParam("releaseVersion") String releaseVersion,
//		@NotEmpty @FormParam("releaseLicense") String releaseLicense,
//		@FormParam("releaseReleased") boolean releaseReleased,
//		@FormParam("releaseFiles") EntityPart uploads,
//		@FormParam("releaseDescription") String releaseDescription
	) {
		boolean projectEditable = controllerUtil.isProjectEditable(project);
		
		if(!projectEditable) {
			throw new NotAuthorizedException("Project is not editable", Response.status(Status.UNAUTHORIZED).build());
		}
		
		String releaseVersion = entityParts.stream()
			.filter(part -> "releaseVersion".equals(part.getName()))
			.findFirst()
			.map(controllerUtil::toString)
			.orElse(null);
		String releaseLicense = entityParts.stream()
			.filter(part -> "releaseLicense".equals(part.getName()))
			.findFirst()
			.map(controllerUtil::toString)
			.orElse(null);
		boolean releaseReleased = entityParts.stream()
			.filter(part -> "releaseReleased".equals(part.getName()))
			.findFirst()
			.map(controllerUtil::toString)
			.map(val -> "true".equals(val))
			.orElse(null);
		String releaseDescription = entityParts.stream()
			.filter(part -> "releaseDescription".equals(part.getName()))
			.findFirst()
			.map(controllerUtil::toString)
			.orElse(null);
		
		var release = new ProjectRelease();
		release.setVersion(releaseVersion);
		release.setLicenseType(releaseLicense);
		release.setReleased(releaseReleased);
		release.setDescription(releaseDescription);
		
		release.setReleaseDate(LocalDate.now());
		release.setProjectName(project.getName());
		release.setMasterChef(List.of(userInfo.getDisplayName()));
		
		List<EntityAttachment> attachments = entityParts.stream()
			.filter(part -> "releaseFiles".equals(part.getName()))
			.map(part -> {
				String fileName = part.getFileName()
					.orElseGet(() -> UUID.randomUUID().toString());
				String type = part.getMediaType().toString();
				byte[] data;
				try {
					data = part.getContent(byte[].class);
				} catch (IllegalArgumentException | IllegalStateException | WebApplicationException | IOException e) {
					throw new RuntimeException(e);
				}
				
				return EntityAttachment.of(fileName, System.currentTimeMillis(), type, data);
			})
			.toList();
		release.setAttachments(attachments);
		
		release = projectReleaseRepository.save(release, true);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/releases/" + release.getDocumentId();
	}
	
	@Path("{release}/{fileName}")
	@GET
	public Response getProjectReleaseFile(@PathParam("fileName") String fileName) throws IOException {
		return controllerUtil.fetchAttachment(release, fileName, request);
	}
}
