package api.atompub.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=AtomPubService.NS_APP)
public class Control {
	private String draft;
	
	public Control() {
	}
	public Control(String draft) {
		this.draft = draft;
	}
	
	@XmlElement(namespace=AtomPubService.NS_APP)
	public String getDraft() {
		return draft;
	}
	public void setDraft(String draft) {
		this.draft = draft;
	}
}
