/**
 * Copyright (c) 2022-2023 Contributors to the OpenNTF Home App Project
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

import java.util.Comparator;
import java.util.stream.Collectors;

import api.external.discord.ScheduledEvent;
import bean.BlogEntries;
import bean.DiscordCacheBean;
import bean.ProjectReleases;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.nosql.mapping.Pagination;
import jakarta.nosql.mapping.Sorts;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.ct.CtEntry;

@Path("/")
@Controller
public class HomeController {
	@Inject
	Models models;
	
	@Inject
	ProjectReleases projectReleases;
	
	@Inject
	BlogEntries blogEntries;
	
	@Inject
	DiscordCacheBean discordBean;
	
	@Inject
	CtEntry.Repository ctEntries;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String get() {
		models.put("recentReleases", projectReleases.getRecentReleases(5)); //$NON-NLS-1$
		models.put("upcomingEvents", //$NON-NLS-1$
			discordBean.getUpcomingEvents()
				.stream()
				.sorted(Comparator.comparing(ScheduledEvent::getScheduledStartTime))
				.collect(Collectors.toList())
		);
		models.put("recentCtPosts", ctEntries.listEntries(Sorts.sorts().desc("creationDate"), Pagination.page(1).size(5)).collect(Collectors.toList()));
		models.put("blogEntries", blogEntries.getEntries(5)); //$NON-NLS-1$
		
		return "home.jsp"; //$NON-NLS-1$
	}

}
