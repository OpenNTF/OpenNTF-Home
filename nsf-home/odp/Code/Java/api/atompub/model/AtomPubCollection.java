package api.atompub.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="collection", namespace=AtomPubService.NS_ATOMPUB)
public class AtomPubCollection {
	private String href;
	private String title;
	private List<String> accept = new ArrayList<>();
	private CategoriesRef categories;
	
	@XmlAttribute
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	@XmlElement(namespace=AtomPubService.NS_ATOM)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@XmlElement(namespace=AtomPubService.NS_ATOMPUB)
	public List<String> getAccept() {
		return accept;
	}
	@XmlElementRef
	public CategoriesRef getCategories() {
		return categories;
	}
	public void setCategoriesHref(String url) {
		CategoriesRef categories = new CategoriesRef();
		categories.setHref(url);
		this.categories = categories;
	}
}
