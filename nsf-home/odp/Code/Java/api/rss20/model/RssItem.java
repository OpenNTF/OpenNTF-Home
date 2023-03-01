package api.rss20.model;

import java.time.Instant;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="item")
public class RssItem {
	
	private String title;
	private String link;
	private String contentEncoded;
	private String guid;
	private String creator;
	private Instant date;
	private String description;

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
	@XmlElement(name="encoded", namespace=Rss.NS_CONTENT)
	public String getContentEncoded() {
		return contentEncoded;
	}
	public void setContentEncoded(String contentEncoded) {
		this.contentEncoded = contentEncoded;
		this.description = contentEncoded;
	}
	@XmlElement
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	@XmlElement(name="creator", namespace=Rss.NS_DC)
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	@XmlElement(name="date", namespace=Rss.NS_DC)
	@XmlJavaTypeAdapter(InstantXmlAdapter.class)
	public Instant getDate() {
		return date;
	}
	public void setDate(Instant date) {
		this.date = date;
	}
	@XmlElement
	public String getDescription() {
		return description;
	}
	
}
