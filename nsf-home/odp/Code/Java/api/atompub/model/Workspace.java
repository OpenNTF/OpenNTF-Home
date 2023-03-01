package api.atompub.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=AtomPubService.NS_ATOMPUB)
public class Workspace {
	private String title;
	private List<AtomPubCollection> collections = new ArrayList<>();
	
	@XmlElement(namespace=AtomPubService.NS_ATOM)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@XmlElementRef
	public List<AtomPubCollection> getCollections() {
		return collections;
	}
}
