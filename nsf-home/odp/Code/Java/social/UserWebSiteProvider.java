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
package social;

import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.social.impl.AbstractPeopleDataProvider;
import com.ibm.xsp.extlib.social.impl.PersonImpl;

import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import lotus.domino.Directory;
import lotus.domino.DirectoryNavigator;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class UserWebSiteProvider extends AbstractPeopleDataProvider {
	public static final String FIELD_WEBSITE = "webSite";
	
	@Override
	public int getWeight() {
		return WEIGHT_LOW;
	}

	@Override
	public Object getValue(PersonImpl person, Object key) {
		switch(StringUtil.toString(key)) {
		case FIELD_WEBSITE:
			String id = person.getId();
			if(StringUtil.isNotEmpty(id) && !"anonymous".equalsIgnoreCase(id)) {
				// Try to find their email address
				try {
					Session session = CDI.current().select(Session.class, NamedLiteral.of("dominoSession")).get();
					Directory dir = session.getDirectory();
					DirectoryNavigator nav = dir.lookupNames("($Users)", id, "WebSite");
					if(nav.findFirstMatch()) {
						List<?> vals = nav.getFirstItemValue();
						if(vals != null && !vals.isEmpty()) {
							return StringUtil.toString(vals.get(0));
						}
					}
				} catch(NotesException e) {
					throw new RuntimeException(e);
				}
			}
			return null;
		default:
			return null;
		}
	}

	@Override
	public Class<?> getType(PersonImpl person, Object key) {
		switch(StringUtil.toString(key)) {
		case FIELD_WEBSITE:
			return String.class;
		default:
			return Object.class;
		}
	}

	@Override
	public void readValues(PersonImpl[] persons) {
		
	}


}
