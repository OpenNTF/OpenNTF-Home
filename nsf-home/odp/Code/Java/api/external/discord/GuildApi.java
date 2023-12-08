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
