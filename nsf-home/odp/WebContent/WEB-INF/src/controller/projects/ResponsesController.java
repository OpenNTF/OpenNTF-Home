package controller.projects;

import java.time.OffsetDateTime;
import java.util.List;

import bean.MarkdownBean;
import bean.UserInfoBean;
import bean.UsersBean.UserInfo;
import controller.ControllerUtil;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.EntityPart;
import model.projects.Project;
import model.projects.Response;
import util.StringUtil;

@Path("projects/{project}/responses/{entityId}")
@Controller
public class ResponsesController {
	
	@Inject
	private Models models;
	
    @PathParam("project")
    private Project project;
    
    @Inject
    private Response.Repository responseRepository;
    
    @Inject
    private ControllerUtil controllerUtil;
    
    @Inject
    private MarkdownBean markdownBean;
    
    @Inject
    private UserInfoBean userInfo;
	
	@Path("@new")
	@GET
	@View("project/response-edit.jsp")
	public void compose(@PathParam("entityId") String entityId) {
		models.put("project", project); //$NON-NLS-1$
		models.put("entityId", entityId); //$NON-NLS-1$
		models.put("doc", new Response()); //$NON-NLS-1$
	}
	
	@Path("@new")
	@POST
	public String create(@PathParam("entityId") String entityId, List<EntityPart> parts) {
		var doc = new Response();
		
		String body = null;
		for(var part : parts) {
			switch(String.valueOf(part.getName())) {
			case "body" -> body = controllerUtil.toString(part); //$NON-NLS-1$
			}
		}

		doc.setProjectName(project.getName());
		doc.setBodyMarkdown(body);
		doc.setBody(markdownBean.toHtml(body));
		
		// TODO figure out MainID
		
		if(StringUtil.isEmpty(doc.getDocumentId())) {
			// Then it's new - configure some defaults
			doc.setDate(OffsetDateTime.now());
			doc.setAuthor(userInfo.getDisplayName());
		}
		
		responseRepository.save(doc, true);

		// TODO figure out link to entity type
		return null;
	}
}
