package api.atompub.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="category", namespace=AtomPubService.NS_ATOM)
public class AtomCategory {
	private String term;
	
	public AtomCategory() {
	}
	public AtomCategory(String term) {
		this.term = term;
	}
	
	@XmlAttribute
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
}
