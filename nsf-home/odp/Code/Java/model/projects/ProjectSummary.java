package model.projects;

import org.openntf.xsp.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.nosql.mapping.extension.RepositoryProvider;

import jakarta.nosql.mapping.Entity;

/**
 * This entity is used to represent view entries in a view that lists
 * main projects plus a screenshot, in order to facilitate an API
 * listing summary information
 * 
 * @author Jesse Gallagher
 */
@Entity("ProjectSummary")
public class ProjectSummary {
	@RepositoryProvider("projectsRepository")
	public static interface Repository extends DominoRepository<ProjectSummary, String> {
		
	}
}
