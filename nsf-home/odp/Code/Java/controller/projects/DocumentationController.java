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

import bean.MarkdownBean;
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
import jakarta.ws.rs.ext.RuntimeDelegate;
import model.projects.Documentation;
import model.projects.Project;
import rest.ext.ValidProjectRelationship;
import util.StringUtil;

@Path("projects/{project}/documentation")
@ValidProjectRelationship
public class DocumentationController {
	
	@Inject
	private Models models;
	
	@Inject
	private Documentation.Repository documentationRepository;
	
	@Inject
	private ControllerUtil controllerUtil;

    @Context
    private Request request;
    
    @PathParam("project")
    private Project project;
    
    @Inject
    private MarkdownBean markdownBean;
    
    @Inject
    private UserInfoBean userInfo;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/documentations.jsp")
	public void get() {
		models.put("project", project);
	}

    @Path("{doc}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getEntity(@PathParam("doc") Documentation doc) {
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(doc.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		models.put("project", project);
		
		models.put("doc", doc);
		
		return Response.ok(new Viewable("project/documentations.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
	}
	
	@Path("{doc}/{fileName}")
	@GET
	public Response getFile(@PathParam("doc") Documentation doc, @PathParam("fileName") String fileName) throws IOException {
    	return controllerUtil.fetchAttachment(doc, fileName, request);
	}
	
	@Path("{doc}/@edit")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/documentation-edit.jsp")
	@RolesAllowed("login")
	public void edit(@PathParam("doc") Documentation doc) {
		controllerUtil.validateEditable(project, doc);
		
		models.put("project", project);
		
		if(StringUtil.isEmpty(doc.getBodyMarkdown())) {
			doc.setBodyMarkdown(doc.getBody());
		}
		models.put("doc", doc);
	}
	
	@Path("@new")
	@GET
	@Controller
	@Produces(MediaType.TEXT_HTML)
	@View("project/documentation-edit.jsp")
	@RolesAllowed("login")
	public void compose() {
		controllerUtil.validateEditable(project);
		
		models.put("project", project);
		
		var doc = new Documentation();
		doc.setAttachments(new ArrayList<>());
		models.put("doc", doc);
	}
	
	@Path("{doc}")
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Controller
	@RolesAllowed("login")
	public String deleteProjectRelease(@PathParam("doc") Documentation doc) {
		controllerUtil.validateEditable(project, doc);
		
		documentationRepository.delete(doc);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/documentation";
	}
	
	@Path("{doc}")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Controller
	@RolesAllowed("login")
	public String update(@PathParam("doc") Documentation doc, List<EntityPart> entityParts) {
		controllerUtil.validateEditable(project, doc);
		
		var updated = updateFromPayload(doc, entityParts);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/documentation/" + updated.getDocumentId();
	}
	
	@Path("@new")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Controller
	@RolesAllowed("login")
	public String create(List<EntityPart> entityParts) {
		controllerUtil.validateEditable(project);
		
		var doc = updateFromPayload(new Documentation(), entityParts);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/documentation/" + doc.getDocumentId();
	}
	
	private Documentation updateFromPayload(Documentation doc, List<EntityPart> entityParts) {
		String docName = null;
		String docBody = null;
		Set<String> deleteAttachments = new HashSet<>();
		List<EntityAttachment> attachments = new ArrayList<>();
		for(EntityPart part : entityParts) {
			switch(String.valueOf(part.getName())) {
			case "docName" -> docName = controllerUtil.toString(part);
			case "docBody" -> docBody = controllerUtil.toString(part);
			case "deleteAttachments" -> deleteAttachments.add(controllerUtil.toString(part));
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

		doc.setProjectName(project.getName());
		doc.setDescription(docName);
		doc.setBodyMarkdown(docBody);
		doc.setBody(markdownBean.toHtml(docBody));
		
		if(StringUtil.isEmpty(doc.getDocumentId())) {
			// Then it's new - configure some defaults
			doc.setEntryDate(LocalDate.now());
			doc.setEntryAuthor(userInfo.getDisplayName());
		}
		
		var existingAttachments = doc.getAttachments();
		if(existingAttachments != null) {
			existingAttachments.stream()
				.filter(att -> !deleteAttachments.contains(att.getName()))
				.forEach(attachments::add);
		}
		doc.setAttachments(attachments);
		
		return documentationRepository.save(doc, true);
	}
}
