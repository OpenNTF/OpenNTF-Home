package api.rss20.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="link", namespace=Rss.NS_ATOM)
public class AtomLink {
	private String href;
	private String rel;
	private String type;
	
	@XmlAttribute
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	@XmlAttribute
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	@XmlAttribute
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
