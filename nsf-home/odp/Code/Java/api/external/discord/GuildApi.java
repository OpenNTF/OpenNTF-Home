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

import java.util.List;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(baseUri="https://discord.com/api")
@Path("guilds/{guildId}")
@RegisterProvider(DiscordAuthorization.class)
public interface GuildApi {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject getGuild(@PathParam("guildId") String guildId);
	
	@Path("scheduled-events")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ScheduledEvent> getEvents(@PathParam("guildId") String guildId);
}
