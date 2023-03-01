package api.rss20.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="channel")
public class Channel {
	private String title;
	private String link;
	private String description;
	private Image image = new Image();
	private List<RssItem> items = new ArrayList<>();
	private List<AtomLink> links = new ArrayList<>();

	@XmlElement
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@XmlElement
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	@XmlElement
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlElementRef
	public Image getImage() {
		return image;
	}
	@XmlElementRef
	public List<RssItem> getItems() {
		return items;
	}
	@XmlElementRef
	public List<AtomLink> getLinks() {
		return links;
	}
}