package api.atompub;

import java.net.URI;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import api.atompub.model.AtomPubCollection;
import api.atompub.model.AtomPubService;
import api.atompub.model.Workspace;
import api.rsd.RSD;
import api.rsd.RSDService;
import bean.UrlBean;
import bean.UserInfoBean;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path(AtomPubResource.BASE_PATH)
@RolesAllowed(UserInfoBean.ROLE_BLOGADMIN)
@RSD(name="AtomPub", basePath=AtomPubResource.BASE_PATH, preferred=false)
@RequestScoped
public class AtomPubResource implements RSDService {
	public static final String BASE_PATH = "/atompub"; //$NON-NLS-1$

	public static final String BLOG_ID = "openntf";

	@Inject @Named("translation")
	ResourceBundle translation;
	@Context
	ServletContext servletContext;
	@Context
	HttpServletRequest servletRequest;
	@Context
	UriInfo uriInfo;
	@Inject
	UrlBean urlBean;

	// This only supports the one active blog
	@GET
	@Produces("application/atomserv+xml")
	public AtomPubService getWorkspace() {
		AtomPubService service = new AtomPubService();
		
		Workspace workspace = service.getWorkspace();
		workspace.setTitle(BLOG_ID);

		// Blog posts collection
		{
			AtomPubCollection collection = new AtomPubCollection();
			workspace.getCollections().add(collection);
			collection.setHref(resolveUrl(BLOG_ID));
			collection.setTitle("Entries");
			collection.setCategoriesHref(resolveUrl(BLOG_ID, "categories"));
		}

		// Media collection
		{
			AtomPubCollection collection = new AtomPubCollection();
			workspace.getCollections().add(collection);
			collection.setHref(resolveUrl(BLOG_ID, MediaResource.PATH));
			collection.setTitle("Pictures");
			Stream.of(
				"image/png", //$NON-NLS-1$
				"image/jpeg", //$NON-NLS-1$
				"image/gif", //$NON-NLS-1$
				"image/webp" //$NON-NLS-1$
			).forEach(collection.getAccept()::add);
		}

		return service;
	}

	private String resolveUrl(final String... parts) {
		URI baseUri = uriInfo.getBaseUri();
		String uri = urlBean.concat(baseUri.toString(), BASE_PATH);
		for(String part : parts) {
			uri = urlBean.concat(uri, part);
		}
		return uri;
	}
}
