/**
 * Copyright (c) 2022-2024 Contributors to the OpenNTF Home App Project
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

import java.util.Arrays;

import com.ibm.commons.util.PathUtil;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.beans.DominoDBUserBeanDataProvider;
import com.ibm.xsp.extlib.beans.UserBean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@RequestScoped @Named("userInfo")
public class UserInfoBean {
	public static final String ROLE_BLOGADMIN = "[BlogAdmin]";
	public static final String ROLE_CONTRIBUTOR = "[Contributor]";
	
	@Inject
	private UserBean userBean;
	
	@Inject
	private HttpServletRequest request;
	
	public String getUserName() {
		return userBean.getId();
	}
	
	public String getDisplayName() {
		return userBean.getDisplayName();
	}
	
	public boolean isAnonymous() {
		String name = getUserName();
		return StringUtil.isEmpty(name) || "anonymous".equalsIgnoreCase(name);
	}
	
	public String getThumbnailUrl() {
		String url = userBean.getThumbnailUrl();
		if(url != null && url.startsWith("/.ibmxspres")) {
			return PathUtil.concat("/xsp", url, '/');
		} else if(url != null && url.startsWith("/")) {
			return PathUtil.concat(request.getContextPath(), url, '/');
		} else {
			return url;
		}
	}
	
	public boolean isApprovedContributor() {
		String[] roles = (String[])userBean.getValue(DominoDBUserBeanDataProvider.FIELD_DBACL_ACCESS_ROLES);
		return roles != null && Arrays.binarySearch(roles, ROLE_CONTRIBUTOR) > -1;
	}
}
