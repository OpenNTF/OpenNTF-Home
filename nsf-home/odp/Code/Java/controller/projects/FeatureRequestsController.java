package controller.projects;

import org.eclipse.krazo.engine.Viewable;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
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
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/requests.jsp")
	public void list() {
		models.put("project", project);
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
}
