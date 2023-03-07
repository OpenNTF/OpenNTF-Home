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

import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.commons.util.StringUtil;

/**
 * This bean is intended to be a JSP utility bean for text encoding.
 */
@RequestScoped @Named("encoder")
public class EncoderBean {
	
	@Inject @Named("dominoSession")
	private Session session;
	
	/**
	 * URL-encodes the provided value, using {@link URLEncoder#encode(String, String)}.
	 *
	 * @param value the value to URL-encode
	 * @return the URL-encoded value
	 */
	public String urlEncode(final String value) {
		if(StringUtil.isEmpty(value)) {
			return StringUtil.EMPTY_STRING;
		} else {
			try {
				return URLEncoder.encode(value, "UTF-8");
			} catch(UnsupportedEncodingException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
	
	/**
	 * Cleans a document-stored {@link model.home.Page Page} ID to be displayed
	 * to humans.
	 * 
	 * @param pageId the ID to clean
	 * @return a cleaned version of the ID
	 */
	public String cleanPageId(String pageId) {
		return pageId.replace('_', ' ');
	}
	
	/**
	 * Converts a name to Domino-style "abbreviated" format.
	 * 
	 * @param name the name to convert, such as one in Domino canonical format
	 * @return an abbreviated form of the name
	 */
	public String abbreviateName(String name) throws NotesException {
		Name dominoName = session.createName(name);
		try {
			return dominoName.getAbbreviated();
		} finally {
			dominoName.recycle();
		}
	}
	
	/**
	 * Extracts the common name from a potentially-hierarchical Domino name
	 * 
	 * @param name the name to convert, such as one in Domino canonical format
	 * @return an abbreviated form of the name
	 */
	public String toCommonName(String name) throws NotesException {
		Name dominoName = session.createName(name);
		try {
			return dominoName.getCommon();
		} finally {
			dominoName.recycle();
		}
	}
}