package controller.projects;

import bean.EncoderBean;
import controller.ControllerUtil;
import jakarta.inject.Inject;
import jakarta.mvc.Models;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import model.projects.Project;
import model.projects.ProjectRelative;

/**
 * Parent class for project-component controllers. Subclasses should
 * have a {@link Path} annotation at the class level consisting of
 * "projects/{project}/somepath" for the base listing page.
 * 
 * @param <T> the type of project child this manages
 */
public abstract class AbstractProjectController<T extends ProjectRelative> {
	
	@Inject
	protected Models models;
	
	@Inject
	protected ControllerUtil controllerUtil;
	
    @PathParam("project")
    protected Project project;

    @Context
    protected Request request;
    
    @Inject
    protected EncoderBean encoder;
    
    public void list() {
		models.put("project", project); //$NON-NLS-1$
    }
    
    protected void compose(T doc) {
    	controllerUtil.validateEditable(project);
		
		models.put("project", project); //$NON-NLS-1$
		
		models.put("doc", doc); //$NON-NLS-1$
    }
    
    protected void edit(T doc) {
    	controllerUtil.validateEditable(project, doc);
	
		models.put("project", project); //$NON-NLS-1$
		models.put("doc", doc); //$NON-NLS-1$
    }
    
    /**
     * Constructs an MVC redirect string for the base URL of the controller.
     * 
     * @return a redirect string for the base URL
     */
    protected String redirect() {
    	Class<?> clazz = getClass();
		if(clazz.getName().contains("$Proxy$")) { //$NON-NLS-1$
			clazz = clazz.getSuperclass();
		}
    	String path = clazz.getAnnotation(Path.class).value();
    	return "redirect:" + path.replace("{project}", encoder.urlEncode(project.getName())); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * Constructs an MVC redirect string to show the specified document.
     * 
     * @param doc the document to redirect to
     * @return a redirect string for the base URL
     */
    protected String redirect(T doc) {
    	return redirect() + '/' + encoder.urlEncode(doc.getDocumentId());
    }
}
