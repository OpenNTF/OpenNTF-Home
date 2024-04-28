package api.rss20;

import java.time.Instant;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import api.rss20.model.AtomLink;
import api.rss20.model.Channel;
import api.rss20.model.Image;
import api.rss20.model.Rss;
import api.rss20.model.RssItem;
import bean.EncoderBean;
import bean.MarkdownBean;
import bean.ProjectReleases;
import bean.TemporalBean;
import bean.UrlBean;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.nosql.mapping.Pagination;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import lotus.domino.NotesException;
import model.blog.BlogEntry;
import model.projects.ProjectRelease;

@Path("/projectReleases.xml")
public class ProjectFeedResource {
	@Inject
	ProjectReleases projectReleases;
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
	TemporalBean temporalBean;
	@Inject
	MarkdownBean markdown;

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
		channel.setTitle(translation.getString("releaseFeedTitle")); //$NON-NLS-1$
		channel.setDescription(translation.getString("releaseFeedDescription")); //$NON-NLS-1$
		channel.setLink(baseUrl);
		
		AtomLink self = new AtomLink();
		self.setRel("self");
		self.setType("application/rss+xml");
		self.setHref(urlBean.concat(baseUrl, "projectReleases.xml"));
		channel.getLinks().add(self);
		
		Image image = channel.getImage();
		image.setUrl(urlBean.concat(baseUrl, "img/icon.png")); //$NON-NLS-1$
		image.setTitle(translation.getString("appTitle")); //$NON-NLS-1$
		image.setLink(baseUrl);

		projectReleases.getRecentReleaseDocuments(20)
			.stream()
			.map(release -> toEntry(release, baseUrl))
			.forEach(channel.getItems()::add);

		return rss;
	}

	private RssItem toEntry(final ProjectRelease release, final String baseUrl) {
		RssItem entry = new RssItem();

		String author;
		try {
			List<String> chefs = release.getMasterChef();
			if(chefs == null || chefs.isEmpty()) {
				author = "";
			} else {
				author = encoderBean.toCommonName(chefs.stream().findFirst().orElse(""));
			}
		} catch (NotesException e) {
			throw new RuntimeException(e);
		}
		entry.setCreator(author);

		entry.setTitle(release.getDisplayName());
		entry.setLink(urlBean.concat(baseUrl, "projects", encoderBean.urlEncode(release.getProjectName()), "releases", release.getDocumentId()));
		entry.setGuid(entry.getLink());
		entry.setDate(temporalBean.toInstant(release.getReleaseDate()));

		entry.setTitle(release.getDisplayName());
		entry.setContentEncoded(markdown.toHtml(release.getDescription()));

		return entry;
	}
}
