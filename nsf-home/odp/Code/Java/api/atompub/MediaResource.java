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
package api.atompub;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

import api.atompub.model.Author;
import api.atompub.model.Content;
import api.atompub.model.Entry;
import api.atompub.model.Feed;
import api.atompub.model.Link;
import api.atompub.model.Summary;
import bean.EncoderBean;
import bean.UrlBean;
import bean.UserInfoBean;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import model.blog.BlogPhoto;

@Path(AtomPubResource.BASE_PATH + "/{blogId}/" + MediaResource.PATH)
@RolesAllowed(UserInfoBean.ROLE_BLOGADMIN)
public class MediaResource {
    public static final String PATH = "media"; //$NON-NLS-1$
    public static final String ALBUM = "AtomPub Uploads";

    @Inject
    @Named("translation")
    ResourceBundle translation;

    @Context
    UriInfo uriInfo;

    @Context
    HttpServletRequest request;

    @Inject
    BlogPhoto.Repository mediaRepository;
    
    @Inject
    UserInfoBean userInfo;
    
	@Inject
	UrlBean urlBean;
	
	@Inject
	EncoderBean encoder;

    @GET
    @Produces("application/atom+xml")
    public Feed list() {
    	Feed feed = new Feed();

        mediaRepository.findAll()
        	.map(m -> {
        		Entry entry = new Entry();
                populateAtomXml(entry, m);
        		return entry;
        	})
        	.forEach(feed.getEntries()::add);

        return feed;
    }

    @POST
    @Produces("application/atom+xml")
    public Response uploadMedia(final byte[] data) throws URISyntaxException {
        String contentType = request.getContentType();
        String name = request.getHeader("Slug"); //$NON-NLS-1$

        // TODO make sure it's not already there
        // This could use the name as the UNID, but it's kind of nice having a "real" UNID behind the scenes
        BlogPhoto media = mediaRepository.findByTitle(name).orElseGet(BlogPhoto::new);
        media.setTitle(name);
        media.setAttachments(Arrays.asList(EntityAttachment.of(name, System.currentTimeMillis(), contentType, data)));
        media.setAuthor(userInfo.getUserName());
        media.setPhotoDate(LocalDate.now());
        media.setAlbum(ALBUM);
        media = mediaRepository.save(media);

        // Force update of metadata fields
        media = mediaRepository.findById(media.getDocumentId()).get();

        return Response.created(new URI(resolveUrl(AtomPubResource.BLOG_ID, PATH, media.getDocumentId())))
        	.entity(toAtomXml(media))
        	.build();
    }

    @GET
    @Path("{mediaId}")
    @Produces("application/atom+xml")
    public Entry getMediaInfo(@PathParam("mediaId") final String mediaId) {
    	BlogPhoto media = mediaRepository.findById(mediaId).orElseThrow(NotFoundException::new);
        return toAtomXml(media);
    }

    @GET
    @Path("{mediaId}/{name}")
    public Response getMedia(@PathParam("mediaId") final String mediaId) throws IOException {
    	BlogPhoto media = mediaRepository.findById(mediaId).orElseThrow(NotFoundException::new);
    	EntityAttachment att = media.getAttachments().get(0);

        return Response.ok(att.getData()).header(HttpHeaders.CONTENT_TYPE, att.getContentType()).build();
    }

    private Entry toAtomXml(final BlogPhoto media) {
    	Entry entry = new Entry();
        populateAtomXml(entry, media);
        return entry;
    }

    private void populateAtomXml(final Entry entry, final BlogPhoto media) {
    	entry.setTitle(media.getTitle());
    	entry.setId(media.getDocumentId());
    	entry.setUpdated(media.getModified());
    	entry.setAuthor(new Author(media.getAuthor()));
    	
    	Summary summary = new Summary();
    	summary.setType("text");
    	summary.setBody(media.getTitle());
    	entry.setSummary(summary);

        EntityAttachment att = media.getAttachments().get(0);
        Content content = new Content();
        content.setType(att.getContentType());
        entry.setContent(content);

        String nameEnc = encoder.urlEncode(media.getTitle());
		// TODO remove explicit "xsp/app"
        String path = urlBean.concat("xsp/app", MediaResource.PATH, media.getDocumentId(), nameEnc);
        content.setSrc(path);
        entry.setContent(content);

        Link editMediaLink = new Link();
        editMediaLink.setEditMedia(resolveUrl(AtomPubResource.BLOG_ID, PATH, media.getDocumentId(), nameEnc));
        entry.getLinks().add(editMediaLink);

        Link editLink = new Link();
        editLink.setRel("link");
        editLink.setEditMedia(resolveUrl(AtomPubResource.BLOG_ID, PATH, media.getDocumentId()));
        entry.getLinks().add(editLink);
    }

    private String resolveUrl(final String... parts) {
        URI baseUri = uriInfo.getBaseUri();
        String uri = urlBean.concat(baseUri.toString(), AtomPubResource.BASE_PATH);
        for(String part : parts) {
            uri = urlBean.concat(uri, part);
        }
        return uri;
    }
}
