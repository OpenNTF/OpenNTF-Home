package rest.ext;

import controller.ControllerUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Models;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ext.ParamConverter;
import model.projects.FeatureRequest;

@ApplicationScoped
public class FeatureRequestParamConverter implements ParamConverter<FeatureRequest> {
	
	@Inject
	private FeatureRequest.Repository repository;
	
	@Inject
	private Models models;
	
	@Inject
	private ControllerUtil controllerUtil;

	@Override
	public FeatureRequest fromString(String value) {
		String key = value.replace('+', ' ');
		var result = repository.findById(key)
			.orElseThrow(() -> new NotFoundException("Unable to find feature request for ID: " + key));
		models.put("docEditable", controllerUtil.isEditable(result));
		return result;
	}

	@Override
	public String toString(FeatureRequest value) {
		return value.getDocumentId();
	}

}
