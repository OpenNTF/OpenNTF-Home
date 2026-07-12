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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.krazo.engine.Viewable;

import bean.EncoderBean;
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
import model.projects.Project;
import model.projects.Review;

@Path("projects")
public class ProjectsController {
	
	@Inject
	private Models models;
	
	@Inject
	private Project.Repository projectRepository;
	
	@Inject
	private Discussion.Repository discussionRepository;
	
	@Inject
	private Defect.Repository defectRepository;
	
	@Inject
	private Review.Repository reviewRepository;

    @Context
    private Request request;
    
    @PathParam("project")
    private Project project;
    
    @Inject
    private ControllerUtil controllerUtil;
    
    @Inject
    private EncoderBean encoder;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("projects.jsp")
	public void get(@QueryParam("page") Integer page, @QueryParam("sort") String sort) {
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
	}

	@Path("{project}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/summary.jsp")
	public void getProject() {
		models.put("project", project);
		
		models.put("editable", controllerUtil.isProjectEditable(project));
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

		return encoder.urlFormat("redirect:projects/%s", project.getName()); //$NON-NLS-1$
	}
	
	@Path("{project}/defects")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/defects.jsp")
	public void getProjectDefects() {
		models.put("project", project);
	}
	
	@Path("{project}/defects/{documentId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectDefect(@PathParam("documentId") String documentId) {
		var doc = defectRepository.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Defect for ID {0}", documentId)));

		controllerUtil.validateRelatives(project, doc);
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(doc.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		models.put("project", project);
		
		models.put("doc", doc);
		
		return Response.ok(new Viewable("project/defects.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
	}
	
	@Path("{project}/discussions")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/discussions.jsp")
	public void getProjectDiscussions() {
		models.put("project", project);
	}
	
	@Path("{project}/discussion/{discussionId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectDiscussion(@PathParam("discussionId") String discussionId) {
		Discussion discussion = discussionRepository.findById(discussionId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Discussion for ID {0}", discussionId)));

		controllerUtil.validateRelatives(project, discussion);
		
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
	@View("project/reviews.jsp")
	public void getProjectReviews() {
		models.put("project", project);
	}
	
	@Path("{project}/reviews/{documentId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectReview(@PathParam("documentId") String documentId) {
		Review review = reviewRepository.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Review for ID {0}", documentId)));

		controllerUtil.validateRelatives(project, review);
		
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
	@View("project/sourceControl.jsp")
	public void getProjectSourceControl() {
		models.put("project", project);
	}
}
