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

import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

@RequestScoped
public class TranslationBean {
	@Inject
	HttpServletRequest request;

	@Produces @Named("translation")
	public ResourceBundle getTranslation() {
		return ResourceBundle.getBundle("translation", request.getLocale()); //$NON-NLS-1$
	}

	@RequestScoped
	@Named("messages")
	public static class Messages {
		@Inject
		HttpServletRequest request;

		public String format(final String key, final Object... params) {
			ResourceBundle translation = CDI.current().select(ResourceBundle.class, NamedLiteral.of("translation")).get(); //$NON-NLS-1$
			String message = translation.getString(key);
			return MessageFormat.format(message, params);
		}
		
		/**
		 * Formats the keyed message from the translation bundle with the provided
		 * parameters if they key exists. If it doesn't, this returns the key
		 * itself.
		 * 
		 * @param key the key to look up in the translation bundle
		 * @param params the parameters to use when formatting
		 * @return a formatted message if the key has a translation, or the key
		 *         itself otherwise
		 */
		public String softFormat(final String key, final Object... params) {
			ResourceBundle translation = CDI.current().select(ResourceBundle.class, NamedLiteral.of("translation")).get(); //$NON-NLS-1$
			if(translation.containsKey(key)) {
				String message = translation.getString(key);
				return MessageFormat.format(message, params);
			} else {
				return key;
			}
		}

		public String getMonth(final int index) {
			return DateFormatSymbols.getInstance(request.getLocale()).getMonths()[index];
		}
	}
}