package rest.ext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.inject.Inject;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import model.projects.Project;
import model.projects.ProjectRelease;

@Provider
public class ProjectParamConverterProvider implements ParamConverterProvider {
	
	@Inject
	private ProjectParamConverter projectParamConverter;
	
	@Inject
	private ProjectReleaseParamConverter releaseParamConverter;

	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
		if(Project.class.equals(rawType)) {
			return (ParamConverter<T>)projectParamConverter;
		} else if(ProjectRelease.class.equals(rawType)) {
			return (ParamConverter<T>)releaseParamConverter;
		}
		return null;
	}

}
