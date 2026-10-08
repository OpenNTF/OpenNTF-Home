package model.projects;

/**
 * This interface describes an entity that is associated to a parent
 * project by name.
 */
public interface ProjectRelative {
	String getProjectName();
	
	String getDocumentId();
}
