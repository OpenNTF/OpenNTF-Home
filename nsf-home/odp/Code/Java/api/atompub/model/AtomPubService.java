package api.atompub.model;

import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="service", namespace=AtomPubService.NS_ATOMPUB)
public class AtomPubService {
	public static final String NS_ATOMPUB = "http://purl.org/atom/app#";
	public static final String NS_ATOM = "http://www.w3.org/2005/Atom";
	public static final String NS_APP = "http://www.w3.org/2007/app";
	
	private Workspace workspace = new Workspace();
	
	@XmlElementRef
	public Workspace getWorkspace() {
		return workspace;
	}
}
