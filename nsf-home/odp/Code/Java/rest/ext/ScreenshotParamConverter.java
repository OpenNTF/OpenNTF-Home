package rest.ext;

import java.text.MessageFormat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ext.ParamConverter;
import model.projects.Screenshot;

@ApplicationScoped
public class ScreenshotParamConverter implements ParamConverter<Screenshot> {
	@Inject
	private Screenshot.Repository repository;
	
	@Override
	public Screenshot fromString(String value) {
		return repository.findById(value)
			.orElseThrow(() -> new NotFoundException(MessageFormat.format("Unable to find Screenshot for ID {0}", value)));
	}

	@Override
	public String toString(Screenshot value) {
		return value.getDocumentId();
	}

}
