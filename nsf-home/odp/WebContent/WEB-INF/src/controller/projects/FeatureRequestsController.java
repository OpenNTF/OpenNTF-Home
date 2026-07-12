package controller.projects;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import model.projects.Documentation;
import model.projects.FeatureRequest;
import model.projects.Project;
import rest.ext.ValidProjectRelationship;
import util.StringUtil;

@Path("projects/{project}/requests")
@ValidProjectRelationship
@Controller
public class FeatureRequestsController {
	public enum Filter {
		submitted,
		investigating,
		rejected,
		added,
		all
	}
	public static record FilterNode(Filter filter, boolean active, long count, String messageKey) {}
	
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
	@View("project/requests.jsp")
	public void list(@QueryParam("filter") String filterParam) {
		pushRequestsContext(filterParam);
	}
	
	@Path("@new")
	@GET
	@View("project/request-edit.jsp")
	@RolesAllowed("login")
	public void compose() {
		controllerUtil.validateEditable(project);
		
		models.put("project", project); //$NON-NLS-1$
		
		var featureRequest = new FeatureRequest();
		featureRequest.setAttachments(new ArrayList<>());
		models.put("featureRequest", featureRequest); //$NON-NLS-1$
	}
	
	@Path("{request}/@edit")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/request-edit.jsp")
	@RolesAllowed("login")
	public void edit(@PathParam("request") FeatureRequest featureRequest) {
		controllerUtil.validateEditable(project, featureRequest);
		
		models.put("project", project); //$NON-NLS-1$
		
		if(StringUtil.isEmpty(featureRequest.getBodyMarkdown())) {
			featureRequest.setBodyMarkdown(featureRequest.getBody());
		}
		models.put("featureRequest", featureRequest); //$NON-NLS-1$
	}
	
	@Path("{request}")
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Controller
	@RolesAllowed("login")
	public String delete(@PathParam("request") FeatureRequest featureRequest) {
		controllerUtil.validateEditable(project, featureRequest);
		
		requestRepository.delete(featureRequest);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/requests";
	}
	
	@Path("{request}")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Controller
	@RolesAllowed("login")
	public String update(@PathParam("request") FeatureRequest featureRequest, List<EntityPart> entityParts) {
		controllerUtil.validateEditable(project, featureRequest);
		
		var updated = updateFromPayload(featureRequest, entityParts);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/requests/" + updated.getDocumentId();
	}
	
	@Path("{request}")
	@GET
	@View("project/requests.jsp")
	public void show(@PathParam("request") FeatureRequest featureRequest, @QueryParam("filter") String filterParam) {
		pushRequestsContext(filterParam);
		
		models.put("featureRequest", featureRequest); //$NON-NLS-1$
		models.put("requestEditable", controllerUtil.isEditable(featureRequest)); //$NON-NLS-1$
		
		var responses = responseRepository.findTree(ViewQuery.query().category(featureRequest.getDocumentId()))
			.skip(1) // the main doc
			.map(entry -> {
				// Re-fetch the doc to get all the data
				var doc = responseRepository.findById(entry.getDocumentId()).get();
				doc.setViewPosition(entry.getViewPosition());
				return doc;
			}).toList();
		models.put("responses", responses); //$NON-NLS-1$
	}
	
	@Path("{request}/@changeStatus")
	@POST
	public String changeStatus(@PathParam("request") FeatureRequest featureRequest, @FormParam("status") FeatureRequest.Status status) {
		controllerUtil.validateEditable(project, featureRequest);
		
		featureRequest.setStatus(status);
		requestRepository.save(featureRequest, true);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/requests/" + featureRequest.getDocumentId(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Path("@new")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@RolesAllowed("login")
	public String create(List<EntityPart> entityParts) {
		controllerUtil.validateEditable(project);
		
		var featureRequest = updateFromPayload(new FeatureRequest(), entityParts);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8) + "/requests/" + featureRequest.getDocumentId(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private FeatureRequest updateFromPayload(FeatureRequest doc, List<EntityPart> entityParts) {
		String subject = null;
		String body = null;
		for(EntityPart part : entityParts) {
			switch(String.valueOf(part.getName())) {
			case "subject" -> subject = controllerUtil.toString(part); //$NON-NLS-1$
			case "body" -> body = controllerUtil.toString(part); //$NON-NLS-1$
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
	
	private void pushRequestsContext(String filterParam) {
		ViewQuery query = ViewQuery.query().category(project.getName());
		
		Filter[] activeFilter = new Filter[] { Filter.all };
		var filters = Arrays.stream(Filter.values())
			.map(f -> {
				var active = f.name().equalsIgnoreCase(filterParam);
				
				// Do a sneaky side effect to also just set the active filter for page use
				if(active) {
					activeFilter[0] = f;
				}

				// Find the count based on known mappings
				int count = switch(f) {
					case added -> requestRepository.listAddedEntries(query).findFirst().map(FeatureRequest::getSiblingCount).orElse(0);
					case all -> requestRepository.listAllEntries(query).findFirst().map(FeatureRequest::getSiblingCount).orElse(0);
					case investigating -> requestRepository.listInvestigatingEntries(query).findFirst().map(FeatureRequest::getSiblingCount).orElse(0);
					case rejected -> requestRepository.listRejectedEntries(query).findFirst().map(FeatureRequest::getSiblingCount).orElse(0);
					case submitted -> requestRepository.listSubmittedEntries(query).findFirst().map(FeatureRequest::getSiblingCount).orElse(0);
					default -> 0;
				};
				
				return new FilterNode(f, active, count, f.name()+"Filter"); //$NON-NLS-1$
			})
			.toList();
		models.put("filters", filters); //$NON-NLS-1$
		
		if(activeFilter[0] != Filter.all) {
			models.put("filterQuery", "filter=" + activeFilter[0]); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		models.put("project", project); //$NON-NLS-1$
		var requests = switch(activeFilter[0]) {
			case added -> requestRepository.listAdded(query).toList();
			case all -> requestRepository.listAll(query).toList();
			case investigating -> requestRepository.listInvestigating(query).toList();
			case rejected -> requestRepository.listRejected(query).toList();
			case submitted -> requestRepository.listSubmitted(query).toList();
			default -> List.of();
		};
		models.put("featureRequests", requests); //$NON-NLS-1$
	}
}
