package rest.ext;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.openntf.xsp.jakarta.rest.RestClassContributor;

import jakarta.mvc.security.Csrf;

public class AppRestClassContributor implements RestClassContributor {

	@Override
	public Collection<Class<?>> getClasses() {
		return Collections.emptySet();
	}
	
	@Override
	public Map<String, Object> getProperties() {
		// TODO see if this can be made to work properly
		return Map.of(Csrf.CSRF_PROTECTION, Csrf.CsrfOptions.OFF);
	}

}
