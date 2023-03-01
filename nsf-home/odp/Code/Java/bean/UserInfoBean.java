package bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@RequestScoped @Named("userInfo")
public class UserInfoBean {
	public static final String ROLE_BLOGADMIN = "[BlogAdmin]";
	
	@Inject
	private HttpServletRequest request;
	
	public String getUserName() {
		return request.getUserPrincipal().getName();
	}
}
