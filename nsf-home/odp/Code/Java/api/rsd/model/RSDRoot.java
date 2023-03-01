package api.rsd.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="rsd", namespace=RSDRoot.NS_RSD)
public class RSDRoot {
	public static final String NS_RSD = "http://archipelago.phrasewise.com/rsd";
	
	private String version = "1.0";
	private List<Service> services = new ArrayList<>();
	
	@XmlAttribute
	public String getVersion() {
		return version;
	}
	
	@XmlElementRef
	public List<Service> getServices() {
		return services;
	}
}