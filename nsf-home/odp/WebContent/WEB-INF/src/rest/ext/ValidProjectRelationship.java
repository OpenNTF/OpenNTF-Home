package rest.ext;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * This validation constraint ensures that, if the class contains a
 * {@link model.projects.Project Project} property, all of the
 * {@link model.projects.ProjectRelative ProjectRelative} properties
 * are related to that project 
 */
@Constraint(validatedBy = ProjectRelationshipValidator.class)
@Retention(RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ValidProjectRelationship {
	String message() default "Project relatives must match the context project";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
