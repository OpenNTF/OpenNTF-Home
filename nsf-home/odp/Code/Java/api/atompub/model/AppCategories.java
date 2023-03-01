package api.atompub.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="categories", namespace=AtomPubService.NS_APP)
public class AppCategories {
	private boolean fixed;
	private List<AtomCategory> categories = new ArrayList<>();
	
	@XmlAttribute
	public boolean isFixed() {
		return fixed;
	}
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	@XmlElementRef
	public List<AtomCategory> getCategories() {
		return categories;
	}
}
