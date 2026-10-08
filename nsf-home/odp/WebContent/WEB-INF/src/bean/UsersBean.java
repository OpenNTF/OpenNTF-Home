package bean;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import controller.ControllerUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import util.AppUtil;

@ApplicationScoped
@Named("usersBean")
public class UsersBean extends AbstractMap<String, UsersBean.UserInfo> {
	public record UserInfo(String thumbnailUrl) {
		public String getCleanThumbnailUrl() {
			var req = CDI.current().select(HttpServletRequest.class).get();
			var controllerUtil = CDI.current().select(ControllerUtil.class).get();
			return controllerUtil.cleanThumbnailUrl(thumbnailUrl, req.getContextPath());
		}
	}
	
	private Map<String, UserInfo> cache;
	
	@PostConstruct
	public void init() {
		this.cache = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	@Override
	public UserInfo get(Object key) {
		return AppUtil.computeIfAbsent(cache, String.valueOf(key), k -> {
			return new UserInfo(AppUtil.getGravatarUrl(k));
		});
	}

	@Override
	public Set<Entry<String, UserInfo>> entrySet() {
		return Collections.emptySet();
	}
	
	
}
