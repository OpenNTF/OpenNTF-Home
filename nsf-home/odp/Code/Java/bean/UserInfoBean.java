/**
 * Copyright (c) 2022-2025 Contributors to the OpenNTF Home App Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bean;

import java.security.Principal;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.SecurityContext;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import util.AppUtil;

@RequestScoped @Named("userInfo")
public class UserInfoBean {
	public static final String ROLE_BLOGADMIN = "[BlogAdmin]";
	public static final String ROLE_CONTRIBUTOR = "[Contributor]";
	
	@Inject
	private SecurityContext securityContext;
	
	@Inject @Named("dominoSession")
	private Session session;
	
	public String getUserName() {
		Principal principal = securityContext.getUserPrincipal();
		return principal == null ? "Anonymous" : principal.getName();
	}
	
	public String getDisplayName() {
		Principal principal = securityContext.getUserPrincipal();
		try {
			if(principal != null) {
				Name name = session.createName(principal.getName());
				try {
					return name.getCommon();
				} finally {
					name.recycle();
				}
			} else {
				return "Anonymous";
			}
		} catch(NotesException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isAnonymous() {
		Principal principal = securityContext.getUserPrincipal();
		return principal == null || "Anonymous".equalsIgnoreCase(principal.getName());
	}
	
	public String getThumbnailUrl() {
		String id = getUserName();
		return AppUtil.getGravatarUrl(id);
	}
	
	public boolean isApprovedContributor() {
		return securityContext.isUserInRole(ROLE_CONTRIBUTOR);
	}
}
