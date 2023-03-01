package api.rss20.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="rss")
public class Rss {
	public static final String NS_CONTENT = "http://purl.org/rss/1.0/modules/content/";
	public static final String NS_DC = "http://purl.org/dc/elements/1.1/";
	public static final String NS_ATOM = "http://www.w3.org/2005/Atom";
	
	private String version = "2.0";
	private String base;
	private Channel channel = new Channel();

	@XmlElementRef
	public Channel getChannel() {
		return channel;
	}
	@XmlAttribute(name="version")
	public String getVersion() {
		return version;
	}
	@XmlAttribute(name="xml:base")
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
}
