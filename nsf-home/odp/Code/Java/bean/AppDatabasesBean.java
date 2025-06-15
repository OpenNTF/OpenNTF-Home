/**
 * Copyright (c) 2022-2024 Contributors to the OpenNTF Home App Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bean;

import org.openntf.xsp.jakarta.nosql.communication.driver.lsxbe.impl.DefaultDominoDocumentCollectionManager;

import java.util.HashMap;
import java.util.Map;

import org.openntf.xsp.jakarta.nosql.communication.driver.DominoDocumentManager;

import com.ibm.xsp.model.domino.DominoUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.jnosql.mapping.DatabaseType;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

@RequestScoped
public class AppDatabasesBean {
	@Inject
	private ApplicationConfig config;
	
	private Map<String, Database> openDatabases;
	
	@PostConstruct
	public void init() {
		this.openDatabases = new HashMap<>();
	}
	
	@Produces @Named("projectsDatabase")
	public Database getProjectsDatabase() {
		return openDatabase(config.getProjectsDbPath());
	}
	
	@Produces @Named("blogDatabase")
	public Database getBlogDatabase() {
		return openDatabase(config.getBlogDbPath());
	}
	
	@Produces @Named("homeDatabase")
	public Database getHomeDatabase() {
		return openDatabase(config.getHomeDbPath());
	}
	
	@Produces @Named("webinarsDatabase")
	public Database getWebinarsDatabase() {
		return openDatabase(config.getWebinarsDbPath());
	}
	
	@Produces @Named("ctDatabase")
	public Database getCtDatabase() {
		return openDatabase(config.getCtDbPath());
	}
	
	@Produces @Named("snippetsDatabase")
	public Database getSnippetsDatabase() {
		return openDatabase(config.getSnippetsDbPath());
	}
	
	// NoSQL repositories

	@Produces
	@org.eclipse.jnosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "projectsRepository")
	public DominoDocumentManager getProjectsManager() {
		return new DefaultDominoDocumentCollectionManager(
			this::getProjectsDatabase,
			this::getSessionAsSigner
		);
	}
	
	@Produces
	@org.eclipse.jnosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "blogRepository")
	public DominoDocumentManager getBlogManager() {
		return new DefaultDominoDocumentCollectionManager(
			this::getBlogDatabase,
			this::getSessionAsSigner
		);
	}

	@Produces
	@org.eclipse.jnosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "homeRepository")
	public DominoDocumentManager getHomeManager() {
		return new DefaultDominoDocumentCollectionManager(
			this::getHomeDatabase,
			this::getSessionAsSigner
		);
	}

	@Produces
	@org.eclipse.jnosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "webinarsRepository")
	public DominoDocumentManager getWebinarsManager() {
		return new DefaultDominoDocumentCollectionManager(
			this::getWebinarsDatabase,
			this::getSessionAsSigner
		);
	}

	@Produces
	@org.eclipse.jnosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "ctRepository")
	public DominoDocumentManager getCtManager() {
		return new DefaultDominoDocumentCollectionManager(
			this::getCtDatabase,
			this::getSessionAsSigner
		);
	}

	@Produces
	@org.eclipse.jnosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "snippetsRepository")
	public DominoDocumentManager getSnippetsManager() {
		return new DefaultDominoDocumentCollectionManager(
			this::getSnippetsDatabase,
			this::getSessionAsSigner
		);
	}
	
	private Database openDatabase(String apiPath) {
		return openDatabases.computeIfAbsent(apiPath, key -> {
			try {
				return DominoUtils.openDatabaseByName(getSession(), key);
			} catch(NotesException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	private Session getSession() {
		return CDI.current().select(Session.class, NamedLiteral.of("dominoSession")).get();
	}
	
	private Session getSessionAsSigner() {
		return CDI.current().select(Session.class, NamedLiteral.of("dominoSessionAsSigner")).get();
	}
}
