package api.atompub.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=AtomPubService.NS_ATOM)
public class Author {
	private String name;
	
	public Author() {
	}
	public Author(String name) {
		this.name = name;
	}
	
	@XmlElement(namespace=AtomPubService.NS_ATOM)
	public String getName() {
		return name;
	}
}
