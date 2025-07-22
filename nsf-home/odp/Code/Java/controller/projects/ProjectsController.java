/**
 * Copyright (c) 2022-2025 Contributors to the OpenNTF Home App Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controller.projects;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.krazo.engine.Viewable;

import controller.ControllerUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.data.Sort;
import jakarta.data.page.PageRequest;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;
import model.projects.Defect;
import model.projects.Discussion;
import model.projects.Documentation;
import model.projects.FeatureRequest;
import model.projects.Project;
import model.projects.Review;
import model.projects.Screenshot;

@Path("projects")
public class ProjectsController {
	
	@Inject
	private Models models;
	
	@Inject
	private Project.Repository projectRepository;
	
	@Inject
	private Discussion.Repository discussionRepository;
	
	@Inject
	private Documentation.Repository documentationRepository;
	
	@Inject
	private FeatureRequest.Repository requestRepository;
	
	@Inject
	private Defect.Repository defectRepository;
	
	@Inject
	private Review.Repository reviewRepository;
	
	@Inject
	private Screenshot.Repository screenshotRepository;

    @Context
    private Request request;
    
    @PathParam("project")
    private Project project;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String get(@QueryParam("page") Integer page, @QueryParam("sort") String sort) {
		String sortColumn;
		Sort<Project> sorts = null;
		switch(String.valueOf(sort)) {
		case "name":
			sortColumn = "name";
			sorts = Sort.asc("name");
			break;
		case "owner":
			sorts = Sort.asc("chefs");
			sortColumn = "owner";
			break;
		default:
			sortColumn = "updated";
			break;
		}
		models.put("sortColumn", sortColumn);
		
		int pageSize = 30;
		PageRequest pagination;
		int nextPage = 0;
		int prevPage = 0;
		if(page == null || page < 2) {
			pagination = PageRequest.ofPage(1).size(pageSize);
			nextPage = 2;
		} else {
			pagination = PageRequest.ofPage(page).size(pageSize);
			nextPage = page+1;
			prevPage = page-1;
		}
		List<Project> projects = projectRepository.findAll(pagination, sorts).collect(Collectors.toList());
		if(projects.size() < pageSize) {
			nextPage = 0;
		}
		models.put("projectList", projects);
		models.put("nextPage", nextPage);
		models.put("prevPage", prevPage);
		
		return "projects.jsp";
	}

	@Path("{project}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/summary.jsp")
	public void getProject() {
		models.put("project", project);
		
		models.put("editable", ControllerUtil.isProjectEditable(project));
	}
	
	@Path("{project}/edit")
	@GET
	@Controller
	@View("project/summary-edit.jsp")
	// Users who can edit may not have a specific role in this app, but this is a first check
	@RolesAllowed("login")
	public void editProject() {
		models.put("project", project);
	}
	
	@Path("{project}/edit")
	@POST
	@Controller
	@RolesAllowed("login")
	public String updateProject(
		@FormParam("projectName") @NotEmpty String newProjectName,
		@FormParam("projectOverview") String newProjectOverview,
		@FormParam("projectChefs") String newProjectChefs,
		@FormParam("projectDetails") String newProjectDetails
	) {
		project.setName(newProjectName);
		project.setOverview(newProjectOverview);
		List<String> newChefs = Arrays.stream(newProjectChefs.split(","))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.toList();
		project.setChefs(newChefs);
		project.setDetails(newProjectDetails);
		
		project = projectRepository.save(project, true);
		
		return "redirect:projects/" + URLEncoder.encode(project.getName(), StandardCharsets.UTF_8);
	}
	
	
	
	@Path("{project}/screenshots")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectScreenshots() {
		models.put("project", project);
		
		return "project/screenshots.jsp";
	}
	
	@Path("{project}/screenshots/{screenshotId}/{fileName}")
	@GET
	public Response getProjectScreenshot(@PathParam("screenshotId") String screenshotId, @PathParam("fileName") String fileName) throws IOException {
		Screenshot shot = screenshotRepository.findById(screenshotId).orElseThrow(NotFoundException::new);

    	return ControllerUtil.fetchAttachment(shot, fileName, request);
	}
	
	@Path("{project}/documentation")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectDocumentation() {
		models.put("project", project);
		
		return "project/documentations.jsp";
	}
	
	@Path("{project}/documentation/{documentId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectDocumentationEntry(@PathParam("documentId") String documentId) {
		Documentation doc = documentationRepository.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Documentation for ID {0}", documentId)));
		
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
	
	@Path("{project}/documentation/{documentId}/{fileName}")
	@GET
	public Response getProjectDocumentationFile(@PathParam("documentId") String documentId, @PathParam("fileName") String fileName) throws IOException {
		Documentation doc = documentationRepository.findById(documentId).orElseThrow(NotFoundException::new);
    	
    	return ControllerUtil.fetchAttachment(doc, fileName, request);
	}
	
	@Path("{project}/requests")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectRequests() {
		models.put("project", project);
		
		return "project/requests.jsp";
	}
	
	@Path("{project}/requests/{documentId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectRequestEntry(@PathParam("documentId") String documentId) {
		FeatureRequest featureRequest = requestRepository.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Request for ID {0}", documentId)));
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(featureRequest.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		models.put("project", project);
		
		models.put("featureRequest", featureRequest);
		
		return Response.ok(new Viewable("project/requests.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
	}
	
	@Path("{project}/defects")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectDefects() {
		models.put("project", project);
		
		return "project/defects.jsp";
	}
	
	@Path("{project}/defects/{documentId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectDefect(@PathParam("documentId") String documentId) {
		Defect defect = defectRepository.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Defect for ID {0}", documentId)));
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(defect.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		models.put("project", project);
		
		models.put("defect", defect);
		
		return Response.ok(new Viewable("project/defects.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
	}
	
	@Path("{project}/discussions")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectDiscussions() {
		models.put("project", project);
		
		return "project/discussions.jsp";
	}
	
	@Path("{project}/discussion/{discussionId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectDiscussion(@PathParam("discussionId") String discussionId) {
		Discussion discussion = discussionRepository.findById(discussionId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Discussion for ID {0}", discussionId)));
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(discussion.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		models.put("project", project);
		
		models.put("discussion", discussion);
		
		return Response.ok(new Viewable("project/discussions.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
	}
	
	@Path("{project}/reviews")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectReviews() {
		models.put("project", project);
		
		return "project/reviews.jsp";
	}
	
	@Path("{project}/reviews/{documentId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectReview(@PathParam("documentId") String documentId) {
		Review review = reviewRepository.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Review for ID {0}", documentId)));
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(review.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		models.put("project", project);
		
		models.put("review", review);
		
		return Response.ok(new Viewable("project/reviews.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
	}
	
	@Path("{project}/sourceControl")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectSourceControl() {
		models.put("project", project);
		
		return "project/sourceControl.jsp";
	}
}
