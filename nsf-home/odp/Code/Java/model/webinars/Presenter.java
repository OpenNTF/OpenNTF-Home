package model.webinars;

import java.util.Optional;

import org.openntf.xsp.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.nosql.mapping.extension.RepositoryProvider;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

@Entity("ninSpeaker")
public class Presenter {
	@RepositoryProvider("webinarsRepository")
	public interface Repository extends DominoRepository<Presenter, String> {
		Optional<Presenter> findByPresenterId(String presenterId);
	}
	
	@Id
	private String documentId;
	@Column("fullname")
	private String fullName;
	@Column("company")
	private String company;
	@Column("docId")
	private String presenterId;
	
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getPresenterId() {
		return presenterId;
	}
	public void setPresenterId(String presenterId) {
		this.presenterId = presenterId;
	}
}
