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

import bean.EncoderBean;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.home.Page;

@Path("/pages")
public class PagesController {
	
	@Inject
	Models models;
	
	@Inject
	Page.Repository pageRepository;
	
	@Inject
	EncoderBean encoderBean;

	@Path("{pageId}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Controller
	public String get(@PathParam("pageId") String pageId) {
		String key = encoderBean.cleanPageId(pageId);
		Page page = pageRepository.findBySubject(key)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find page for ID: {0}", key)));
		models.put("page", page); //$NON-NLS-1$
		return "page.jsp"; //$NON-NLS-1$
	}

}