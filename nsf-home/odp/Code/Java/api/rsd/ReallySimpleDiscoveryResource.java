package api.rsd;

import java.util.ResourceBundle;

import api.atompub.AtomPubResource;
import api.rsd.model.Api;
import api.rsd.model.RSDRoot;
import api.rsd.model.Service;
import bean.UrlBean;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path("/rsd.xml")
public class ReallySimpleDiscoveryResource {
	@Inject @Named("translation")
	ResourceBundle translation;

	@Context
	UriInfo uriInfo;
	
	@Inject
	UrlBean urlBean;

	@GET
	@Produces("application/rsd+xml")
	public RSDRoot get() {
		RSDRoot rsd = new RSDRoot();
		
		Service service = new Service();
		service.setEngineName(translation.getString("appTitle"));
		service.setEngineLink(uriInfo.getBaseUri().toString());
		service.setHomePageLink(uriInfo.getBaseUri().toString());
		rsd.getServices().add(service);

		CDI.current().select(RSDService.class)
			.stream()
			.map(Object::getClass)
			.map(c -> {
				// This should unwrap Weld proxy classes
				if(c.isSynthetic()) {
					return c.getSuperclass();
				} else {
					return c;
				}
			})
			.filter(clazz -> clazz.isAnnotationPresent(RSD.class))
			.map(clazz -> {
				RSD def = clazz.getAnnotation(RSD.class);
				Api api = new Api();
				api.setName(def.name());
				api.setPreferred(def.preferred());
				
				api.setApiLink(urlBean.concat(uriInfo.getBaseUri().toString(), def.basePath()));
				api.setBlogID(AtomPubResource.BLOG_ID);
					
				return api;
			})
			.forEach(service.getApis()::add);

		return rsd;
	}
}
