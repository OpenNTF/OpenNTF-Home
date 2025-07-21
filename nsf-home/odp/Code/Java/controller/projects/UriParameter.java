package controller.projects;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

/**
 * This qualifier can be used for common URI path parameter model
 * objects such as projects.
 */
@Qualifier
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface UriParameter {
}
