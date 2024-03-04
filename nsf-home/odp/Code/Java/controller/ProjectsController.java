/**
 * Copyright (c) 2022-2024 Contributors to the OpenNTF Home App Project
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
package controller;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.krazo.engine.Viewable;

import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.nosql.mapping.Pagination;
import jakarta.nosql.mapping.Sorts;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
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
import model.projects.ProjectRelease;
import model.projects.Review;

@Path("/projects")
public class ProjectsController {
	
	@Inject
	Models models;
	
	@Inject
	Project.Repository projectRepository;
	
	@Inject
	ProjectRelease.Repository projectReleaseRepository;
	
	@Inject
	Discussion.Repository discussionRepository;
	
	@Inject
	Documentation.Repository documentationRepository;
	
	@Inject
	FeatureRequest.Repository requestRepository;
	
	@Inject
	Defect.Repository defectRepository;
	
	@Inject
	Review.Repository reviewRepository;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String get(@QueryParam("page") Integer page, @QueryParam("sort") String sort) {
		String sortColumn;
		Sorts sorts = null;
		switch(String.valueOf(sort)) {
		case "name":
			sortColumn = "name";
			sorts = Sorts.sorts().asc("name");
			break;
		case "owner":
			sorts = Sorts.sorts().asc("chefs");
			sortColumn = "owner";
			break;
		default:
			sortColumn = "updated";
			break;
		}
		models.put("sortColumn", sortColumn);
		
		int pageSize = 30;
		Pagination pagination;
		int nextPage = 0;
		int prevPage = 0;
		if(page == null || page < 2) {
			pagination = Pagination.page(1).size(pageSize);
			nextPage = 2;
		} else {
			pagination = Pagination.page(page).size(pageSize);
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

	@Path("{projectName}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProject(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		return "project/summary.jsp";
	}
	
	@Path("{projectName}/edit")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String editProject(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		models.put("editMode", true);
		return "project/summary.jsp";
	}
	
	@Path("{projectName}/releases")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectReleases(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		return "project/releases.jsp";
	}

	@Path("{projectName}/releases")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProjectRelease> getProjectReleasesJson(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		return project.getReleasesByDate();
	}
	
	@Path("{projectName}/releases/{releaseId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectRelease(@PathParam("projectName") String projectName, @PathParam("releaseId") String releaseId, @Context Request request, @Context HttpServletResponse resp) {
		ProjectRelease release = projectReleaseRepository.findById(releaseId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Release for ID {0}", releaseId)));
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(release.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		models.put("release", release);
		
		resp.addHeader(HttpHeaders.ETAG, etag.getValue());
		
		return Response.ok(new Viewable("project/releases.jsp")).build();
	}
	
	@Path("{projectName}/screenshots")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectScreenshots(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		return "project/screenshots.jsp";
	}
	
	@Path("{projectName}/documentation")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectDocumentation(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		return "project/documentations.jsp";
	}
	
	@Path("{projectName}/documentation/{documentId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectDocumentationEntry(@PathParam("projectName") String projectName, @PathParam("documentId") String documentId, @Context Request request, @Context HttpServletResponse resp) {
		Documentation doc = documentationRepository.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Documentation for ID {0}", documentId)));
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(doc.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		models.put("doc", doc);
		
		resp.addHeader(HttpHeaders.ETAG, etag.getValue());
		
		return Response.ok(new Viewable("project/documentations.jsp")).build();
	}
	
	@Path("{projectName}/requests")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectRequests(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		return "project/requests.jsp";
	}
	
	@Path("{projectName}/requests/{documentId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectRequestEntry(@PathParam("projectName") String projectName, @PathParam("documentId") String documentId, @Context Request request, @Context HttpServletResponse resp) {
		FeatureRequest featureRequest = requestRepository.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Request for ID {0}", documentId)));
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(featureRequest.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		models.put("featureRequest", featureRequest);
		
		resp.addHeader(HttpHeaders.ETAG, etag.getValue());
		
		return Response.ok(new Viewable("project/requests.jsp")).build();
	}
	
	@Path("{projectName}/defects")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectDefects(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		return "project/defects.jsp";
	}
	
	@Path("{projectName}/defects/{documentId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectDefect(@PathParam("projectName") String projectName, @PathParam("documentId") String documentId, @Context Request request, @Context HttpServletResponse resp) {
		Defect defect = defectRepository.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Defect for ID {0}", documentId)));
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(defect.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		models.put("defect", defect);
		
		resp.addHeader(HttpHeaders.ETAG, etag.getValue());
		
		return Response.ok(new Viewable("project/defects.jsp")).build();
	}
	
	@Path("{projectName}/discussions")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectDiscussions(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		return "project/discussions.jsp";
	}
	
	@Path("{projectName}/discussion/{discussionId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectDiscussion(@PathParam("projectName") String projectName, @PathParam("discussionId") String discussionId, @Context Request request, @Context HttpServletResponse resp) {
		Discussion discussion = discussionRepository.findById(discussionId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Discussion for ID {0}", discussionId)));
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(discussion.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		models.put("discussion", discussion);
		
		resp.addHeader(HttpHeaders.ETAG, etag.getValue());
		
		return Response.ok(new Viewable("project/discussions.jsp")).build();
	}
	
	@Path("{projectName}/reviews")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectReviews(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		return "project/reviews.jsp";
	}
	
	@Path("{projectName}/reviews/{documentId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public Response getProjectReview(@PathParam("projectName") String projectName, @PathParam("documentId") String documentId, @Context Request request, @Context HttpServletResponse resp) {
		Review review = reviewRepository.findById(documentId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Review for ID {0}", documentId)));
		
		EntityTag etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(review.getEtag());
		ResponseBuilder response = request.evaluatePreconditions(etag);
		if(response != null) {
			return response.build();
		}
		
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		models.put("review", review);
		
		resp.addHeader(HttpHeaders.ETAG, etag.getValue());
		
		return Response.ok(new Viewable("project/reviews.jsp")).build();
	}
	
	@Path("{projectName}/sourceControl")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectSourceControl(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		return "project/sourceControl.jsp";
	}
}
