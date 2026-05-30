package controller.projects;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.krazo.engine.Viewable;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import bean.MarkdownBean;
import bean.UserInfoBean;
import controller.ControllerUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;
import model.projects.FeatureRequest;
import model.projects.Project;
import rest.ext.ValidProjectRelationship;
import util.StringUtil;

@Path("projects/{project}/requests")
@ValidProjectRelationship
@Controller
public class FeatureRequestsController {
	
	@Inject
	private Models models;
	
	@Inject
	private FeatureRequest.Repository requestRepository;
	
	@Inject
	private model.projects.Response.Repository responseRepository;

    @Context
    private Request request;

    @PathParam("project")
    private Project project;
	
	@Inject
	private ControllerUtil controllerUtil;
	
	@Inject
	private MarkdownBean markdownBean;
	
	@Inject
	private UserInfoBean userInfo;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/requests.jsp")
	public void list() {
		models.put("project", project);
		var requests = requestRepository.listAll(ViewQuery.query().category(project.getName())).toList();
		models.put("featureRequests", requests);
	}
	
	@Path("{request}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response show(@PathParam("request") FeatureRequest featureRequest) {
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(featureRequest.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		models.put("project", project);
		models.put("featureRequests", project.getFeatureRequests());
		
		models.put("featureRequest", featureRequest);
		
		var responses = responseRepository.findTree(ViewQuery.query().category(featureRequest.getDocumentId()))
			.skip(1) // the main doc
			.map(entry -> {
				// Re-fetch the doc to get all the data
				var doc = responseRepository.findById(entry.getDocumentId()).get();
				doc.setViewPosition(entry.getViewPosition());
				return doc;
			}).toList();
		models.put("responses", responses);
		
		return Response.ok(new Viewable("project/requests.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
	}
	
	@Path("@new")
	@GET
	@Controller
	@Produces(MediaType.TEXT_HTML)
	@View("project/request-edit.jsp")
	@RolesAllowed("login")
	public void compose() {
		controllerUtil.validateEditable(project);
		
		models.put("project", project);
		
		var featureRequest = new FeatureRequest();
		featureRequest.setAttachments(new ArrayList<>());
		models.put("featureRequest", featureRequest);
	}
	
	@Path("@new")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Controller
	@RolesAllowed("login")
	public String create(List<EntityPart> entityParts) {
		controllerUtil.validateEditable(project);
		
		var featureRequest = updateFromPayload(new FeatureRequest(), entityParts);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/requests/" + featureRequest.getDocumentId();
	}
	
	private FeatureRequest updateFromPayload(FeatureRequest doc, List<EntityPart> entityParts) {
		String subject = null;
		String body = null;
		for(EntityPart part : entityParts) {
			switch(String.valueOf(part.getName())) {
			case "subject" -> subject = controllerUtil.toString(part);
			case "body" -> body = controllerUtil.toString(part);
			}
		}

		doc.setProjectName(project.getName());
		doc.setSubject(subject);
		doc.setBodyMarkdown(body);
		doc.setBody(markdownBean.toHtml(body));
		
		if(StringUtil.isEmpty(doc.getDocumentId())) {
			// Then it's new - configure some defaults
			doc.setEntryDate(OffsetDateTime.now());
			doc.setEntryAuthor(userInfo.getDisplayName());
		}
		
		return requestRepository.save(doc, true);
	}
}
