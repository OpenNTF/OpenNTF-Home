/**
 * Copyright (c) 2022-2023 Contributors to the OpenNTF Home App Project
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
	
	@Produces @Named("projectsDatabase")
	public Database getProjectsDatabase() {
		try {
			return DominoUtils.openDatabaseByName(getSession(), config.getProjectsDbPath());
		} catch(NotesException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Produces @Named("blogDatabase")
	public Database getBlogDatabase() {
		try {
			return DominoUtils.openDatabaseByName(getSession(), config.getBlogDbPath());
		} catch(NotesException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Produces @Named("homeDatabase")
	public Database getHomeDatabase() {
		try {
			return DominoUtils.openDatabaseByName(getSession(), config.getHomeDbPath());
		} catch(NotesException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Produces @Named("webinarsDatabase")
	public Database getWebinarsDatabase() {
		try {
			return DominoUtils.openDatabaseByName(getSession(), config.getWebinarsDbPath());
		} catch(NotesException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Produces @Named("ctDatabase")
	public Database getCtDatabase() {
		try {
			return DominoUtils.openDatabaseByName(getSession(), config.getCtDbPath());
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

	@Produces
	@jakarta.nosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "webinarsRepository")
	public DominoDocumentCollectionManager getWebinarsManager() {
		return new DefaultDominoDocumentCollectionManager(
			() -> getWebinarsDatabase(),
			() -> getSessionAsSigner()
		);
	}

	@Produces
	@jakarta.nosql.mapping.Database(value = DatabaseType.DOCUMENT, provider = "ctRepository")
	public DominoDocumentCollectionManager getCtManager() {
		return new DefaultDominoDocumentCollectionManager(
			this::getCtDatabase,
			this::getSessionAsSigner
		);
	}
}
