package rest.ext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import model.projects.Project;
import model.projects.ProjectRelative;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class ProjectRelationshipValidator implements ConstraintValidator<ValidProjectRelationship, Object> {

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		Project project = null;
		List<ProjectRelative> relatives = new ArrayList<>();
		
		Class<?> clazz = value.getClass();
		if(clazz.getName().contains("$Proxy$")) { //$NON-NLS-1$
			clazz = clazz.getSuperclass();
		}
		Field[] fields = clazz.getDeclaredFields();
		
		try {
			for(Field field : fields) {
				if(Project.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					project = (Project)field.get(value);
				} else if(ProjectRelative.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					ProjectRelative rel = (ProjectRelative)field.get(value);
					if(rel != null) {
						relatives.add(rel);
					}
				}
			}
			
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		if(project == null) {
			return relatives.isEmpty();
		}
		
		
		String projectName = project.getName();
		for(ProjectRelative rel : relatives) {
			if(!projectName.equals(rel.getProjectName())) {
				return false;
			}
		}
		
		return true;
	}
}
