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
package api.rsd.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="api", namespace=RSDRoot.NS_RSD)
public class Api {
	private String apiLink;
	private String blogID;
	private String name;
	private boolean preferred;

	@XmlAttribute
	public String getApiLink() {
		return apiLink;
	}
	public void setApiLink(String apiLink) {
		this.apiLink = apiLink;
	}
	@XmlAttribute
	public String getBlogID() {
		return blogID;
	}
	public void setBlogID(String blogID) {
		this.blogID = blogID;
	}
	@XmlAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute
	public boolean isPreferred() {
		return preferred;
	}
	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}
}
