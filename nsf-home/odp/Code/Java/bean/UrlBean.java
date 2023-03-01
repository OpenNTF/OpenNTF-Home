package bean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.net.URISyntaxException;

import com.ibm.commons.util.PathUtil;

@ApplicationScoped
@Named("urlBean")
public class UrlBean {
	@Inject
	private HttpServletRequest req;
	
	public URI getRequestUri() throws URISyntaxException {
		return new URI(req.getRequestURL().toString()).resolve(req.getContextPath() + "/"); //$NON-NLS-1$
	}
	
	public String relativizeUrl(String url) {
		// In practice, the distinction in these URLs is whether or not they start with "/"
		if(url == null || url.isEmpty()) {
			return url;
		}
		
		if(url.charAt(0) == '/') {
			return PathUtil.concat(req.getContextPath(), url, '/');
		} else {
			return url;
		}
	}
	
	public String concat(String... parts) {
		if(parts == null || parts.length == 0) {
			return "";
		}
		String result = parts[0];
		for(int i = 1; i < parts.length; i++) {
			if(!result.endsWith("/")) {
				result += "/";
			}
			String part = parts[i];
			if(part.startsWith("/")) {
				part = part.substring(1);
			}
			result += part;
		}
		return result;
	}
}