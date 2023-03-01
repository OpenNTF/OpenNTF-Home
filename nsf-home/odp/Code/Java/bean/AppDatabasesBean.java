package bean;

import org.openntf.xsp.nosql.communication.driver.lsxbe.impl.DefaultDominoDocumentCollectionManager;
import org.openntf.xsp.nosql.communication.driver.DominoDocumentCollectionManager;

import com.ibm.xsp.model.domino.DominoUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.nosql.mapping.DatabaseType;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

@RequestScoped
public class AppDatabasesBean {
	@Inject
	private ApplicationConfig config;
	
	private Session getSession() {
		return CDI.current().select(Session.class, NamedLiteral.of("dominoSession")).get();
	}
	
	private Session getSessionAsSigner() {
		return CDI.current().select(Session.class, NamedLiteral.of("dominoSessionAsSigner")).get();
	}
	
	@Produces @Named("projects")
	public Database getProjectsDatabase() {
		try {
			return DominoUtils.openDatabaseByName(getSession(), config.getProjectsDbPath());
		} catch(NotesException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Produces @Named("blog")
	public Database getBlogDatabase() {
		try {
			return DominoUtils.openDatabaseByName(getSession(), config.getBlogDbPath());
		} catch(NotesException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Produces @Named("home")
	public Database getHomeDatabase() {
		try {
			return DominoUtils.openDatabaseByName(getSession(), config.getHomeDbPath());
		} catch(NotesException e) {
			throw new RuntimeException(e);
		}
	}
	
	// NoSQL repositories

	@Produces
	@jakarta.nosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "projectsRepository")
	public DominoDocumentCollectionManager getProjectsManager() {
		return new DefaultDominoDocumentCollectionManager(
			() -> getProjectsDatabase(),
			() -> getSessionAsSigner()
		);
	}
	
	@Produces
	@jakarta.nosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "blogRepository")
	public DominoDocumentCollectionManager getBlogManager() {
		return new DefaultDominoDocumentCollectionManager(
			() -> getBlogDatabase(),
			() -> getSessionAsSigner()
		);
	}

	@Produces
	@jakarta.nosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "homeRepository")
	public DominoDocumentCollectionManager getHomeManager() {
		return new DefaultDominoDocumentCollectionManager(
			() -> getHomeDatabase(),
			() -> getSessionAsSigner()
		);
	}
}
