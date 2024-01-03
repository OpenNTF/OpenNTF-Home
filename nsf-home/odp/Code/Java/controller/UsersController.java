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
