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
