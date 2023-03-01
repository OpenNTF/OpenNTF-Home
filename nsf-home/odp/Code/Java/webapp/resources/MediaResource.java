package webapp.resources;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

import com.ibm.commons.util.StringUtil;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

import model.blog.BlogPhoto;

@Path(MediaResource.PATH)
public class MediaResource {
    public static final String PATH = "/media"; //$NON-NLS-1$

    @Context
    Request request;

    @Inject
    BlogPhoto.Repository mediaRepository;

    @GET
    @Path("{mediaId}/{mediaName}")
    public Response get(@PathParam("mediaId") final String mediaId, @PathParam("mediaName") final String mediaName) throws IOException {
    	BlogPhoto media = mediaRepository.findById(mediaId).orElseThrow(NotFoundException::new);
    	
    	String expectedName = mediaName.replace('+', ' ').toLowerCase();
        EntityAttachment att = media.getAttachments()
        	.stream()
        	.filter(a -> StringUtil.toString(a.getName()).toLowerCase().endsWith(expectedName))
        	.findFirst()
        	.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find media {0} in document {1}", mediaName, mediaId)));

        EntityTag etag = new EntityTag(att.getETag());
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if(builder == null) {
            builder = Response.ok(att.getData(), att.getContentType())
                .header(HttpHeaders.ETAG, etag);
        }

        CacheControl cc = new CacheControl();
        cc.setMaxAge(5 * 24 * 60 * 60);

        return builder
                .cacheControl(cc)
                .build();
    }
}