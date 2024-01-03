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
package api.external.discord;

import java.io.IOException;

import bean.ApplicationConfig;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;

public class DiscordAuthorization implements ClientRequestFilter {

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		ApplicationConfig config = CDI.current().select(ApplicationConfig.class).get();
		String val = "Bot " + config.getDiscordBotToken();
		requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, val);
	}

}
