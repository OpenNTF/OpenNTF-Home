package controller.projects;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.projects.Screenshot;
import rest.ext.ValidProjectRelationship;
import util.StringUtil;

@Path("projects/{project}/screenshots")
@ValidProjectRelationship
public class ScreenshotsController extends AbstractProjectController<Screenshot> {
	
	@Inject
	private Screenshot.Repository screenshotRepository;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/screenshots.jsp")
	@Override
	public void list() {
		super.list();
	}
	
	@Path("{doc}/{fileName}")
	@GET
	public Response show(@PathParam("doc") Screenshot doc, @PathParam("fileName") String fileName) throws IOException {
		controllerUtil.validateRelatives(project, doc);
		
		return controllerUtil.fetchAttachment(doc, fileName, request);
	}
	
	@Path("@new")
	@GET
	@Controller
	@View("project/screenshot-edit.jsp")
	@RolesAllowed("login")
	public void compose() {
		var doc = new Screenshot();
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
		
		String description = null;
		List<EntityAttachment> attachments = new ArrayList<>();
		for(EntityPart part : entityParts) {
			switch(String.valueOf(part.getName())) {
			case "description" -> description = controllerUtil.toString(part); //$NON-NLS-1$
			case "files" -> { //$NON-NLS-1$
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
		
		var doc = new Screenshot();
		doc.setProjectName(project.getName());
		doc.setAttachments(attachments);
		doc.setDescription(description);
		
		if(StringUtil.isEmpty(doc.getDocumentId())) {
			doc.setDate(OffsetDateTime.now());
		}
		
		screenshotRepository.save(doc, true);
		
		return redirect();
	}
	
	@Path("{doc}/{fileName}")
	@DELETE
	@Controller
	public String delete(@PathParam("doc") Screenshot doc, @PathParam("fileName") String fileName) throws IOException {
		controllerUtil.validateEditable(project, doc);
		
		List<EntityAttachment> newAttachments = doc.getAttachments()
			.stream()
			.filter(att -> !fileName.equals(att.getName()))
			.toList();
		if(newAttachments.isEmpty()) {
			screenshotRepository.delete(doc);
		} else {
			doc.setAttachments(newAttachments);
			screenshotRepository.save(doc, true);
		}

		return redirect();
	}
}
