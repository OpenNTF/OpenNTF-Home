package controller.projects;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.eclipse.krazo.engine.Viewable;

import bean.MarkdownBean;
import bean.UserInfoBean;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.View;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;
import model.projects.ProjectRelease;
import rest.ext.ValidProjectRelationship;
import util.StringUtil;

@Path("projects/{project}/releases")
@ValidProjectRelationship
public class ReleasesController extends AbstractProjectController<ProjectRelease> {
	
	@Inject
	private ProjectRelease.Repository projectReleaseRepository;
    
    @Inject
    private UserInfoBean userInfo;
    
    @Inject
    private MarkdownBean markdownBean;
    
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/releases.jsp")
	public void list() {
		super.list();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProjectRelease> getReleasesJson() {
		return project.getReleasesByDate();
	}
	
	@Path("{doc}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response show(@PathParam("doc") ProjectRelease doc) {
		controllerUtil.validateRelatives(project, doc);
		
		boolean anon = userInfo.isAnonymous();
		EntityTag etag = null;
		if(anon) {
			etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(doc.getEtag());
			ResponseBuilder response = request.evaluatePreconditions(etag);
			if(response != null) {
				return response.build();
			}
		}
		
		models.put("project", project); //$NON-NLS-1$
		
		models.put("doc", doc); //$NON-NLS-1$

		if(anon) {
			return Response.ok(new Viewable("project/releases.jsp")) //$NON-NLS-1$
				.header(HttpHeaders.ETAG,  etag.getValue())
				.build();
		} else {
			return Response.ok(new Viewable("project/releases.jsp")) //$NON-NLS-1$
				.build();
		}
	}
	
	@Path("{doc}/@edit")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/release-edit.jsp")
	@RolesAllowed("login")
	public void edit(@PathParam("doc") ProjectRelease doc) {
		if(StringUtil.isEmpty(doc.getDescriptionMarkdown())) {
			doc.setDescriptionMarkdown(doc.getDescription());
		}
		super.edit(doc);
	}
	
	@Path("@new")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/release-edit.jsp")
	@RolesAllowed("login")
	public void compose() {
		var doc = new ProjectRelease();
		doc.setAttachments(new ArrayList<>());
		super.compose(doc);
	}
	
	@Path("@new")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Controller
	@RolesAllowed("login")
	public String create(List<EntityPart> entityParts) {
		controllerUtil.validateEditable(project);
		
		var doc = updateFromPayload(new ProjectRelease(), entityParts);

		return redirect(doc);
	}
	
	@Path("{doc}")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Controller
	@RolesAllowed("login")
	public String update(@PathParam("doc") ProjectRelease doc, List<EntityPart> entityParts) {
		controllerUtil.validateEditable(project, doc);
		
		var updated = updateFromPayload(doc, entityParts);

		return redirect(updated);
	}
	
	@Path("{doc}")
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Controller
	@RolesAllowed("login")
	public String delete(@PathParam("doc") ProjectRelease doc) {
		controllerUtil.validateEditable(project, doc);
		
		projectReleaseRepository.delete(doc);

		return redirect();
	}
	
	@Path("{doc}/{fileName}")
	@GET
	public Response getFile(@PathParam("doc") ProjectRelease doc, @PathParam("fileName") String fileName) throws IOException {
		return controllerUtil.fetchAttachment(doc, fileName, request);
	}
	
	private ProjectRelease updateFromPayload(ProjectRelease release, List<EntityPart> entityParts) {
		String releaseVersion = null;
		String releaseLicense = null;
		boolean releaseReleased = false;
		String releaseDescription = null;
		Set<String> deleteAttachments = new HashSet<>();
		List<EntityAttachment> attachments = new ArrayList<>();
		for(EntityPart part : entityParts) {
			switch(String.valueOf(part.getName())) {
			case "releaseVersion" -> releaseVersion = controllerUtil.toString(part); //$NON-NLS-1$
			case "releaseLicense" -> releaseLicense = controllerUtil.toString(part); //$NON-NLS-1$
			case "releaseReleased" -> releaseReleased = Boolean.parseBoolean(controllerUtil.toString(part)); //$NON-NLS-1$
			case "releaseDescription" -> releaseDescription = controllerUtil.toString(part); //$NON-NLS-1$
			case "deleteAttachments" -> deleteAttachments.add(controllerUtil.toString(part)); //$NON-NLS-1$
			case "releaseFiles" -> { //$NON-NLS-1$
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
		release.setDescriptionMarkdown(releaseDescription);
		release.setDescription(markdownBean.toHtml(releaseDescription));
		
		release.setProjectName(project.getName());
		
		if(StringUtil.isEmpty(release.getDocumentId())) {
			// Then it's new - configure some defaults
			release.setReleaseDate(OffsetDateTime.now());
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
