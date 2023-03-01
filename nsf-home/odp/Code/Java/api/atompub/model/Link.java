package api.atompub.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=AtomPubService.NS_ATOM)
public class Link {
	private String editMedia;
	private String rel;
	private String href;
	
	public Link() {
	}
	public Link(String rel, String href) {
		this.rel = rel;
		this.href = href;
	}

	@XmlAttribute(name="edit-media")
	public String getEditMedia() {
		return editMedia;
	}
	public void setEditMedia(String editMedia) {
		this.editMedia = editMedia;
	}
	@XmlAttribute
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	@XmlAttribute
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
}
