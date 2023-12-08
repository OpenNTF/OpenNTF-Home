package controller;

import java.util.stream.Collectors;

import bean.UserInfoBean;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.nosql.mapping.Sorts;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import model.projects.Project;

@Path("users")
@Controller
@RequestScoped
public class UsersController {
	
	@Inject
	private Models models;
	
	@Inject
	private UserInfoBean userInfo;
	
	@Inject
	private Project.Repository projectsRepository;
	
	@Path("@me")
	@GET
	@View("userProfile.jsp")
	@RolesAllowed("login")
	public void getMe() {
		models.put("displayName", userInfo.getDisplayName());
		models.put("thumbnailUrl", userInfo.getThumbnailUrl());
		models.put("approvedContributor", userInfo.isApprovedContributor());
		models.put("projects", projectsRepository.findByChefs(userInfo.getDisplayName(), Sorts.sorts().asc("name")).collect(Collectors.toList()));
	}
	
	@Path("{userName}")
	@GET
	@View("userProfile.jsp")
	public void getUser(@PathParam("userName") String userName) {
		// TODO opt-in only?
		throw new ForbiddenException();
	}
	
}
