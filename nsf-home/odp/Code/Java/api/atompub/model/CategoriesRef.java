package api.atompub.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="categories", namespace=AtomPubService.NS_ATOMPUB)
public class CategoriesRef {
	private String href;
	
	@XmlAttribute
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
}
