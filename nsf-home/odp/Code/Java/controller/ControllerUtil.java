package controller;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

import com.ibm.commons.util.PathUtil;
import com.ibm.commons.util.StringUtil;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import model.AbstractAttachmentEntity;

/**
 * Utility methods useful for controller classes
 */
public enum ControllerUtil {
	;
	
	/**
	 * Creates a {@link Response} object containing either a "no change" cached response
	 * or attachment data for a given entity attachment
	 * 
	 * @param entity the {@link AbstractAttachmentEntity} object containing the attachment
	 * @param fileName the URL-sent name of the attachment to find
	 * @param request the contextual Jakarta Rest request
	 * @return a {@link Response} object to send to the client
	 * @throws IOException if there is a problem reading the attachment data
	 * @throws NotFoundException if the attachment with that name doesn't exist
	 */
	public static Response fetchAttachment(AbstractAttachmentEntity entity, String fileName, Request request) throws IOException {
		String expectedName = fileName.replace('+', ' ').toLowerCase();
        EntityAttachment att = entity.getAttachments()
        	.stream()
        	.filter(a -> StringUtil.toString(a.getName()).toLowerCase().endsWith(expectedName))
        	.findFirst()
        	.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find {0} {1} in document {2}", entity.getClass().getSimpleName(), fileName, entity.getDocumentId())));

        EntityTag etag = new EntityTag(att.getETag());
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if(builder == null) {
            builder = Response.ok(att.getData(), att.getContentType())
                .tag(etag);
        }

        CacheControl cc = new CacheControl();
        cc.setMaxAge(5 * 24 * 60 * 60);

        return builder
            .cacheControl(cc)
            .lastModified(new Date(att.getLastModified()))
            .build();
	}
	
	/**
	 * Cleans the provided user thumbnail URL (such as one from UserBean)
	 * for use in an img element.
	 * 
	 * @param url the URL to clean
	 * @param contextPath the app context path
	 * @return a cleaned URL suitable for use in image sources
	 */
	public static String cleanThumbnailUrl(String url, String contextPath) {
		if(url != null && url.startsWith("/.ibmxspres")) {
//			return PathUtil.concat("/xsp", url, '/');
			return url;
		} else if(url != null && url.startsWith("/")) {
			return PathUtil.concat(contextPath, url, '/');
		} else {
			return url;
		}
	}
}
