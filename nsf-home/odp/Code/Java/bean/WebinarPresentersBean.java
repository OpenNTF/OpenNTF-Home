package bean;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ibm.commons.util.StringUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.webinars.Presenter;

/**
 * Bean intended to be used in UI elements to translate presenter
 * IDs to {@link Presenter} objects.
 */
@RequestScoped
@Named("webinarPresentersBean")
public class WebinarPresentersBean {
	
	@Inject
	private Presenter.Repository presenterRepository;
	
	public Presenter getById(String presenterId) {
		if(StringUtil.isEmpty(presenterId)) {
			return null;
		}
		return presenterRepository.findByPresenterId(presenterId)
			.orElse(null);
	}
	
	public String getDisplayFormat(Collection<String> presenterIds) {
		if(presenterIds == null) {
			return null;
		}
		return presenterIds.stream()
			.map(this::getById)
			.filter(Objects::nonNull)
			.map(Presenter::getFullName)
			.collect(Collectors.joining(", "));
	}
}
