package model.ct;

import java.util.Optional;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity("CtPerson") // Fake form name
public class CtPerson {
	@RepositoryProvider("ctRepository")
	public interface Repository extends DominoRepository<CtPerson, String> {
		@ViewEntries("PersonsByPID")
		Optional<CtPerson> findByPid(ViewQuery query);
	}
	
	
	@Id
	private String id;
	
	@Column("PID")
	private String pid;
	
	@Column("PDisplayName")
	private String displayName;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
}
