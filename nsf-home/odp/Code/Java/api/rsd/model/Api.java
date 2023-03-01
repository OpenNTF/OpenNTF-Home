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
