package api.atompub.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@XmlRootElement(namespace=AtomPubService.NS_ATOM)
public class Summary {
	private String type;
	private String body;
	
	@XmlAttribute
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlValue
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
