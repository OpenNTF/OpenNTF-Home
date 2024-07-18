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
package model.projects;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;

import jakarta.nosql.Entity;

/**
 * This entity is used to represent view entries in a view that lists
 * main projects plus a screenshot, in order to facilitate an API
 * listing summary information
 * 
 * @author Jesse Gallagher
 */
@Entity("ProjectSummary")
public class ProjectSummary {
	@RepositoryProvider("projectsRepository")
	public static interface Repository extends DominoRepository<ProjectSummary, String> {
		
	}
}
