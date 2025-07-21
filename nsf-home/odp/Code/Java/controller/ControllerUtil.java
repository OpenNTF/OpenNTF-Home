/**
 * Copyright (c) 2022-2025 Contributors to the OpenNTF Home App Project
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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

import com.ibm.commons.util.PathUtil;
import com.ibm.commons.util.StringUtil;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import model.AbstractAttachmentEntity;
import model.projects.Project;

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
	
	public static boolean isProjectEditable(Project project) {
		// TODO figure out permissions
		return true;
	}
	
	public static String toString(EntityPart part) {
		try {
			return part.getContent(String.class);
		} catch (IllegalArgumentException | IllegalStateException | WebApplicationException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
