package api.v1;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.openntf.xsp.nosql.mapping.extension.ViewQuery;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import model.home.AllianceMember;

@Path("api/v1/members")
public class MembersResource {
	@Inject
	private AllianceMember.Repository membersRepository;

    @Context
    private Request request;
	
	@Path("active")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<AllianceMember> getActiveMembers() {
		return membersRepository.getActiveMembers().collect(Collectors.toList());
	}
	
	@Path("logo/{name}")
	@GET
	public Response getLogo(@PathParam("name") String name) throws IOException {
		String pName = name.replace('+', ' ');
		AllianceMember member = membersRepository.findActiveMemberByName(ViewQuery.query().key(pName, true))
			.orElseThrow(() -> new NotFoundException());
		List<EntityAttachment> attachments = member.getAttachments();
		if(attachments.isEmpty()) {
			throw new NotFoundException();
		}
		EntityAttachment att = attachments.get(0);
		
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
}
