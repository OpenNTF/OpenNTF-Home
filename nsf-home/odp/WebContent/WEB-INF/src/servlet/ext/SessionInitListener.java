package servlet.ext;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This listener ensures that the HttpSession is initialized for every incoming
 * request, which is not always the case in a Jakarta Module. This is required to
 * make sure that Krazo's default CSRF handling functions properly.
 */
@WebListener
public class SessionInitListener implements ServletRequestListener {
	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		if(sre.getServletRequest() instanceof HttpServletRequest req) {
			req.getSession(true);
		}
	}
}