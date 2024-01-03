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

import java.util.List;
import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="service", namespace=RSDRoot.NS_RSD)
public class Service {
	private String engineName;
	private String engineLink;
	private String homePageLink;
	private List<Api> apis = new ArrayList<>();

	@XmlElement(namespace=RSDRoot.NS_RSD)
	public String getEngineName() {
		return engineName;
	}
	public void setEngineName(String engineName) {
		this.engineName = engineName;
	}
	@XmlElement(namespace=RSDRoot.NS_RSD)
	public String getEngineLink() {
		return engineLink;
	}
	public void setEngineLink(String engineLink) {
		this.engineLink = engineLink;
	}
	@XmlElement(namespace=RSDRoot.NS_RSD)
	public String getHomePageLink() {
		return homePageLink;
	}
	public void setHomePageLink(String homePageLink) {
		this.homePageLink = homePageLink;
	}
	@XmlElementWrapper(name="apis", namespace=RSDRoot.NS_RSD)
	@XmlElementRef
	public List<Api> getApis() {
		return apis;
	}
}
