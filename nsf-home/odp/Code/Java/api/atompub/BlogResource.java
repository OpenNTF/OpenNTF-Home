package api.atompub;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.ibm.commons.util.PathUtil;
import com.ibm.commons.util.StringUtil;

import api.atompub.model.AtomCategory;
import api.atompub.model.Author;
import api.atompub.model.Content;
import api.atompub.model.Control;
import api.atompub.model.Entry;
import api.atompub.model.Feed;
import api.atompub.model.Link;
import bean.UserInfoBean;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.nosql.mapping.Pagination;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import model.blog.BlogEntry;
import model.blog.BlogStatus;
import model.util.PostUtil;
import webapp.controller.BlogController;

@Path(AtomPubResource.BASE_PATH + "/{blogId}")
@RolesAllowed(UserInfoBean.ROLE_BLOGADMIN)
public class BlogResource {
	public static final int PAGE_LENGTH = 100;

	@Inject
	@Named("translation")
	ResourceBundle translation;

	@Context
	UriInfo uriInfo;

	@Inject
	BlogEntry.Repository posts;
	
	@Inject
	UserInfoBean userInfo;

	@GET
	@Produces("application/atom+xml")
	public Feed get(@QueryParam("start") final String startParam) {
		Feed feed = new Feed();
		feed.setTitle(translation.getString("appTitle"));
		feed.setSubtitle(translation.getString("appDescription"));
		feed.setId(resolveUrl(AtomPubResource.BLOG_ID));
		feed.getLinks().add(new Link("alternate", translation.getString("baseUrl")));
		feed.getLinks().add(new Link("first", resolveUrl(AtomPubResource.BLOG_ID)));
		
		// Figure out the starting point
		int start = Math.max(PostUtil.parseStartParam(startParam), 0);
		int page = start / PAGE_LENGTH + 1;
		List<BlogEntry> result = posts.findRecent(Pagination.page(page).size(PAGE_LENGTH)).collect(Collectors.toList());

		if (start + PAGE_LENGTH < PostUtil.getPostCount()) {
			// Then add nav links
			Link next = new Link();
			next.setRel("next"); //$NON-NLS-1$
			next.setHref(resolveUrl(AtomPubResource.BLOG_ID) + "?start=" + (start + PAGE_LENGTH)); //$NON-NLS-1$
			feed.getLinks().add(next);
		}

		result.stream()
			.map(this::toEntry)
			.forEach(feed.getEntries()::add);

		return feed;
	}

	@POST
	@Produces("application/atom+xml")
	public Response post(final Entry entry) throws URISyntaxException {

		BlogEntry post = PostUtil.createPost();
		post.setAuthor(userInfo.getUserName());
		updatePost(post, entry);

		Entry result = toEntry(post);
		return Response.created(new URI(resolveUrl(AtomPubResource.BLOG_ID, post.getUnid())))
			.entity(result)
			.build();
	}

	@GET
	@Path("{entryId}")
	@Produces("application/atom+xml")
	public Entry getEntry(@PathParam("entryId") final String postId) {
		BlogEntry post = posts.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("Unable to find post matching ID " + postId)); //$NON-NLS-1$
		return toEntry(post);
	}

	@PUT
	@Path("{entryId}")
	public Response updateEntry(@PathParam("entryId") final String postId, final Entry entry) {
		BlogEntry post = posts.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("Unable to find post matching ID " + postId)); //$NON-NLS-1$
		updatePost(post, entry);
		return Response.ok().build();
	}

	@DELETE
	@Path("{entryId}")
	public Response deleteEntry(@PathParam("entryId") final String postId) {
		// TODO figure out why this doesn't work with existing posts. I imagine it's to
		// do with Darwino's treatment of editors
		BlogEntry post = posts.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("Unable to find post matching ID " + postId)); //$NON-NLS-1$
		posts.deleteById(post.getUnid());
		return Response.ok().build();
	}

	private Entry toEntry(final BlogEntry post) {
		Entry entry = new Entry();
		entry.setAuthor(new Author(post.getAuthor()));
		entry.setTitle(post.getTitle());
		entry.setPublished(Instant.from(post.getOffsetDateTime()));
		Instant mod = post.getModified();
		entry.setUpdated(mod == null ? entry.getPublished() : mod);
		entry.setTitle(StringUtil.toString(post.getTitle()));
		if (post.getStatus() == BlogStatus.Draft) {
			entry.setControl(new Control("yes"));
		}

		String bodyMarkdown = post.getBodyMarkdown();

		if (StringUtil.isNotEmpty(bodyMarkdown)) {
			Content markdown = new Content();
			markdown.setType("text/markdown"); //$NON-NLS-1$
			markdown.setValue(bodyMarkdown);
			entry.setContent(markdown);
		} else {
			Content content = new Content();
			content.setType(MediaType.TEXT_HTML);
			content.setValue(post.getHtml());
			entry.setContent(content);
		}

		post.getCategories().stream()
			.map(AtomCategory::new)
			.map(entry.getCategories()::add);

		// Add links
		Link read = new Link();
		String postsRoot = BlogController.class.getAnnotation(Path.class).value();
		read.setHref(resolveUrlRoot(postsRoot, post.getUnid()));
		entry.getLinks().add(read);
		Link edit = new Link();
		edit.setHref(resolveUrl(AtomPubResource.BLOG_ID, post.getUnid()));
		edit.setRel("edit"); //$NON-NLS-1$
		entry.getLinks().add(edit);

		return entry;
	}

	private void updatePost(final BlogEntry post, final Entry entry) {
		boolean posted = true;
		if(entry.getControl() != null) {
			posted =!"yes".equals(entry.getControl().getDraft());
		}
		post.setTitle(entry.getTitle());
		post.setBodyMarkdown(entry.getContent().getValue());
//		post.setSummary(entry.getSummary().getBody());
		List<AtomCategory> categories = entry.getCategories();
		if(categories != null) {
			post.setCategories(categories.stream().map(AtomCategory::getTerm).collect(Collectors.toList()));
		}
		post.setStatus(posted ? BlogStatus.Published : BlogStatus.Draft);
		posts.save(post);
	}

	private String resolveUrl(final String... parts) {
		URI baseUri = uriInfo.getBaseUri();
		String uri = PathUtil.concat(baseUri.toString(), AtomPubResource.BASE_PATH, '/');
		for (String part : parts) {
			uri = PathUtil.concat(uri, part, '/');
		}
		return uri;
	}

	private String resolveUrlRoot(final String... parts) {
		URI baseUri = uriInfo.getBaseUri();
		String uri = baseUri.toString();
		for (String part : parts) {
			uri = PathUtil.concat(uri, part, '/');
		}
		return uri;
	}
}
