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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.eclipse.krazo.engine.Viewable;

import bean.UserInfoBean;
import jakarta.annotation.security.RolesAllowed;
import jakarta.data.Sort;
import jakarta.data.page.PageRequest;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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
import model.projects.Defect;
import model.projects.Discussion;
import model.projects.Documentation;
import model.projects.FeatureRequest;
import model.projects.Project;
import model.projects.ProjectRelease;
import model.projects.Review;
import model.projects.Screenshot;

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
	
	@Inject
	Screenshot.Repository screenshotRepository;

    @Context
    Request request;
    
    @Inject
    UserInfoBean userInfo;
	
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

	@Path("{projectName}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/summary.jsp")
	public void getProject(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		models.put("editable", isProjectEditable(project));
	}
	
	@Path("{projectName}/edit")
	@GET
	@Controller
	@View("project/summary-edit.jsp")
	// Users who can edit may not have a specific role in this app, but this is a first check
	@RolesAllowed("login")
	public void editProject(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
	}
	
	@Path("{projectName}/edit")
	@POST
	@Controller
	@RolesAllowed("login")
	public String updateProject(
		@PathParam("projectName") String projectName,
		@FormParam("projectName") @NotEmpty String newProjectName,
		@FormParam("projectOverview") String newProjectOverview,
		@FormParam("projectChefs") String newProjectChefs,
		@FormParam("projectDetails") String newProjectDetails
	) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		
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
	
	@Path("{projectName}/releases")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String getProjectReleases(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		models.put("projectEditable", isProjectEditable(project));
		
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
	public Response getProjectRelease(@PathParam("projectName") String projectName, @PathParam("releaseId") String releaseId) {
		ProjectRelease release = projectReleaseRepository.findById(releaseId)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Release for ID {0}", releaseId)));
		
		boolean anon = userInfo.isAnonymous();
		EntityTag etag = null;
		if(anon) {
			etag = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class).fromString(release.getEtag());
			ResponseBuilder response = request.evaluatePreconditions(etag);
			if(response != null) {
				return response.build();
			}
		}
		
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		models.put("project", project);
		
		models.put("release", release);
		
		models.put("projectEditable", isProjectEditable(project));

		if(anon) {
			return Response.ok(new Viewable("project/releases.jsp"))
				.header(HttpHeaders.ETAG,  etag.getValue())
				.build();
		} else {
			return Response.ok(new Viewable("project/releases.jsp"))
				.build();
		}
	}
	
	@Path("{projectName}/releases/@new")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	@View("project/release-edit.jsp")
	// Users who can edit may not have a specific role in this app, but this is a first check
	@RolesAllowed("login")
	public void composeProjectRelease(@PathParam("projectName") String projectName) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		boolean projectEditable = isProjectEditable(project);
		
		if(!projectEditable) {
			throw new NotAuthorizedException("Project is not editable", Response.status(Status.UNAUTHORIZED).build());
		}
		
		models.put("project", project);
		
		var release = new ProjectRelease();
		release.setAttachments(new ArrayList<>());
		models.put("release", release);
	}
	
	@Path("{projectName}/releases/@new")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Controller
	// Users who can edit may not have a specific role in this app, but this is a first check
	@RolesAllowed("login")
	public String createProjectRelease(
		@PathParam("projectName") String projectName,
		List<EntityPart> entityParts
//		@NotEmpty @FormParam("releaseVersion") String releaseVersion,
//		@NotEmpty @FormParam("releaseLicense") String releaseLicense,
//		@FormParam("releaseReleased") boolean releaseReleased,
//		@FormParam("releaseFiles") EntityPart uploads,
//		@FormParam("releaseDescription") String releaseDescription
	) {
		String key = projectName.replace('+', ' ');
		Project project = projectRepository.findByProjectName(key)
			.orElseThrow(() -> new NotFoundException("Unable to find project for name: " + key));
		boolean projectEditable = isProjectEditable(project);
		
		if(!projectEditable) {
			throw new NotAuthorizedException("Project is not editable", Response.status(Status.UNAUTHORIZED).build());
		}
		
		String releaseVersion = entityParts.stream()
			.filter(part -> "releaseVersion".equals(part.getName()))
			.findFirst()
			.map(this::toString)
			.orElse(null);
		String releaseLicense = entityParts.stream()
			.filter(part -> "releaseLicense".equals(part.getName()))
			.findFirst()
			.map(this::toString)
			.orElse(null);
		boolean releaseReleased = entityParts.stream()
			.filter(part -> "releaseReleased".equals(part.getName()))
			.findFirst()
			.map(this::toString)
			.map(val -> "true".equals(val))
			.orElse(null);
		String releaseDescription = entityParts.stream()
			.filter(part -> "releaseDescription".equals(part.getName()))
			.findFirst()
			.map(this::toString)
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
	
	@Path("{projectName}/releases/{releaseId}/{fileName}")
	@GET
	public Response getProjectReleaseFile(@PathParam("projectName") String projectName, @PathParam("releaseId") String releaseId, @PathParam("fileName") String fileName) throws IOException {
		ProjectRelease shot = projectReleaseRepository.findById(releaseId).orElseThrow(NotFoundException::new);

    	return ControllerUtil.fetchAttachment(shot, fileName, request);
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
	
	@Path("{projectName}/screenshots/{screenshotId}/{fileName}")
	@GET
	public Response getProjectScreenshot(@PathParam("projectName") String projectName, @PathParam("screenshotId") String screenshotId, @PathParam("fileName") String fileName) throws IOException {
		Screenshot shot = screenshotRepository.findById(screenshotId).orElseThrow(NotFoundException::new);

    	return ControllerUtil.fetchAttachment(shot, fileName, request);
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
	public Response getProjectDocumentationEntry(@PathParam("projectName") String projectName, @PathParam("documentId") String documentId) {
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
		
		return Response.ok(new Viewable("project/documentations.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
	}
	
	@Path("{projectName}/documentation/{documentId}/{fileName}")
	@GET
	public Response getProjectDocumentationFile(@PathParam("projectName") String projectName, @PathParam("documentId") String documentId, @PathParam("fileName") String fileName) throws IOException {
		Documentation doc = documentationRepository.findById(documentId).orElseThrow(NotFoundException::new);
    	
    	return ControllerUtil.fetchAttachment(doc, fileName, request);
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
	public Response getProjectRequestEntry(@PathParam("projectName") String projectName, @PathParam("documentId") String documentId) {
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
		
		return Response.ok(new Viewable("project/requests.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
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
	public Response getProjectDefect(@PathParam("projectName") String projectName, @PathParam("documentId") String documentId) {
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
		
		return Response.ok(new Viewable("project/defects.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
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
	public Response getProjectDiscussion(@PathParam("projectName") String projectName, @PathParam("discussionId") String discussionId) {
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
		
		return Response.ok(new Viewable("project/discussions.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
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
	public Response getProjectReview(@PathParam("projectName") String projectName, @PathParam("documentId") String documentId) {
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
		
		return Response.ok(new Viewable("project/reviews.jsp"))
			.header(HttpHeaders.ETAG,  etag.getValue())
			.build();
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
	
	private boolean isProjectEditable(Project project) {
		// TODO figure out permissions
		return true;
	}
	
	private String toString(EntityPart part) {
		try {
			return part.getContent(String.class);
		} catch (IllegalArgumentException | IllegalStateException | WebApplicationException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
