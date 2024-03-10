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

import org.openntf.xsp.nosql.mapping.extension.ViewQuery;

import bean.BlogEntries;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.blog.BlogEntry;

@Path("/blog")
@Controller
public class BlogController {

	@Inject
	Models models;
	
	@Inject
	BlogEntries blogEntries;
	
	@Inject
	BlogEntry.Repository blogRepository;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@View("blog.jsp")
	public void get() {
		models.put("blogEntrySummaries", blogEntries.getEntrySummaries()); //$NON-NLS-1$
	}
	
	@Path("tags/{tag}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@View("blog/byTag.jsp")
	public void getByTag(@PathParam("tag") String tag) {
		models.put("tag", tag); //$NON-NLS-1$
		models.put("blogEntries", blogRepository.findByCategory(ViewQuery.query().category(tag)).collect(Collectors.toList()));
		
	}
	
	@Path("{entryUnid}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@View("blog/blogEntry.jsp")
	public void getEntry(@PathParam("entryUnid") String entryUnid) {
		models.put("entry", blogEntries.getEntry(entryUnid));
	}
	
}