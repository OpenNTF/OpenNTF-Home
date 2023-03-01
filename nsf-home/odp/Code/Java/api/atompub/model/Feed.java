package api.atompub.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=AtomPubService.NS_ATOM)
public class Feed {
	private String title;
	private String subtitle;
	private String id;
	private List<Entry> entries = new ArrayList<>();
	private List<Link> links = new ArrayList<>();
	
	@XmlElement(namespace=AtomPubService.NS_ATOM)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@XmlElement(namespace=AtomPubService.NS_ATOM)
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	@XmlElement(namespace=AtomPubService.NS_ATOM)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlElementRef
	public List<Entry> getEntries() {
		return entries;
	}
	@XmlElementRef
	public List<Link> getLinks() {
		return links;
	}
}
