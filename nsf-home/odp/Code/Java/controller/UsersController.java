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
package controller;

import java.util.List;
import java.util.stream.Collectors;

import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import bean.UserInfoBean;
import jakarta.annotation.security.RolesAllowed;
import jakarta.data.Sort;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import model.projects.Project;
import model.snippets.Snippet;
import util.AppUtil;

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
	
	@Inject
	private Snippet.Repository snippetsRepository;
	
	@Context
	private HttpServletRequest request;
	
	@Inject
	private ControllerUtil controllerUtil;
	
	@Path("@me")
	@GET
	@View("userProfile.jsp")
	@RolesAllowed("login")
	public void getMe() {
		models.put("displayPersonalInfo", true);
		
		models.put("displayName", userInfo.getDisplayName());
		models.put("thumbnailUrl", userInfo.getThumbnailUrl());
		models.put("approvedContributor", userInfo.isApprovedContributor());
		models.put("webPage", AppUtil.getUserWebSite(userInfo.getUserName()));
		
		models.put("projects", projectsRepository.findByChefs(userInfo.getDisplayName(), Sort.asc("name")).collect(Collectors.toList()));
		models.put("snippets", snippetsRepository.findByAuthor(ViewQuery.query().category(userInfo.getDisplayName())).toList());
	}
	
	@Path("{userName}")
	@GET
	@View("userProfile.jsp")
	public void getUser(@PathParam("userName") String userName) {
		String user = userName.replace('+', ' ');
		List<Project> projects = projectsRepository.findByChefs(user, Sort.asc("name")).toList();
		List<Snippet> snippets = snippetsRepository.findByAuthor(ViewQuery.query().category(user)).toList();
		if(projects.isEmpty() && snippets.isEmpty()) {
			throw new NotFoundException();
		}
		
		models.put("displayPersonalInfo", false);
		
		if(!"Anonymous".equalsIgnoreCase(user)) {
			String thumbnailUrl = AppUtil.getGravatarUrl(user);
			models.put("thumbnailUrl", controllerUtil.cleanThumbnailUrl(thumbnailUrl, request.getContextPath()));
			models.put("displayName", AppUtil.toCn(user));
			models.put("webPage", AppUtil.getUserWebSite(user));
		} else {
			models.put("displayName", user);
		}
		
		models.put("projects", projects);
		models.put("snippets", snippets);
	}
	
	
}
