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
package api.rss20;

import java.util.ResourceBundle;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import api.rss20.model.AtomLink;
import api.rss20.model.Channel;
import api.rss20.model.Image;
import api.rss20.model.Rss;
import api.rss20.model.RssItem;
import bean.EncoderBean;
import bean.UrlBean;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.data.page.PageRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import model.blog.BlogEntry;

@Path("/blog.xml")
public class BlogFeedResource {
	@Inject
	BlogEntry.Repository posts;
	@Inject @Named("translation")
	ResourceBundle translation;
	@Context
	HttpServletRequest request;
	@Context
	UriInfo uriInfo;
	@Inject
	UrlBean urlBean;
	@Inject
	EncoderBean encoderBean;

	@Inject
	@ConfigProperty(name="blog.rss-request-urls", defaultValue="false")
	private boolean rssRequestUrls;

	@GET
	@Produces("application/rss+xml")
	public Rss get() {
		String baseUrl;
		if(rssRequestUrls) {
			baseUrl = uriInfo.getBaseUri().toString();
		} else {
			baseUrl = urlBean.concat(translation.getString("baseUrl"), request.getContextPath()); //$NON-NLS-1$
		}
		
		Rss rss = new Rss();
		rss.setBase(baseUrl);
		Channel channel = rss.getChannel();
		channel.setTitle(translation.getString("appTitle")); //$NON-NLS-1$
		channel.setDescription(translation.getString("appDescription")); //$NON-NLS-1$
		channel.setLink(baseUrl);
		
		AtomLink self = new AtomLink();
		self.setRel("self");
		self.setType("application/rss+xml");
		self.setHref(urlBean.concat(baseUrl, "blog.xml"));
		channel.getLinks().add(self);
		
		Image image = channel.getImage();
		image.setUrl(urlBean.concat(baseUrl, "img/icon.png")); //$NON-NLS-1$
		image.setTitle(translation.getString("appTitle")); //$NON-NLS-1$
		image.setLink(baseUrl);

		posts.findRecent(PageRequest.ofPage(1).size(10))
			.map(post -> toEntry(post, baseUrl))
			.forEach(channel.getItems()::add);

		return rss;
	}

	private RssItem toEntry(final BlogEntry post, final String baseUrl) {
		RssItem entry = new RssItem();

		String author = encoderBean.toCommonName(post.getAuthor());
		entry.setCreator(author);

		entry.setTitle(post.getTitle());
		entry.setLink(urlBean.concat(baseUrl, "blog", post.getUnid()));
		entry.setGuid(entry.getLink());
		entry.setDate(post.getOffsetDateTime().toInstant());

		@SuppressWarnings("unused")
		String summary = post.getTitle();
		// TODO consider parsing and manipulating the HTML to have a base for images
		entry.setTitle(post.getTitle());
		entry.setContentEncoded(post.getHtml());

		return entry;
	}
}
