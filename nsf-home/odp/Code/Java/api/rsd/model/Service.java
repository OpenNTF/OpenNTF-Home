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
