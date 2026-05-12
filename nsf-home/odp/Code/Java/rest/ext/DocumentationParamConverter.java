package rest.ext;

import controller.ControllerUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Models;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ext.ParamConverter;
import model.projects.Documentation;

@ApplicationScoped
public class DocumentationParamConverter implements ParamConverter<Documentation> {
	
	@Inject
	private Documentation.Repository documentationRepository;
	
	@Inject
	private Models models;
	
	@Inject
	private ControllerUtil controllerUtil;

	@Override
	public Documentation fromString(String value) {
		String key = value.replace('+', ' ');
		var result = documentationRepository.findById(key)
			.orElseThrow(() -> new NotFoundException("Unable to find documentation for name: " + key));
		models.put("docEditable", controllerUtil.isEditable(result));
		return result;
	}

	@Override
	public String toString(Documentation value) {
		return value.getDocumentId();
	}

}
