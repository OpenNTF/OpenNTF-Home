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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import api.external.discord.GuildApi;
import api.external.discord.ScheduledEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Contains a time-based cache of Discord data to avoid hitting
 * rate limits on the server
 */
@ApplicationScoped
public class DiscordCacheBean {
	private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(5);
	
	@Inject
	@RestClient
	private GuildApi guildApi;
	
	@Inject
	private ApplicationConfig appConfig;
	
	private Map<String, Object> cache;
	private Map<String, Long> lastUpdate;
	private Object lock;
	
	@PostConstruct
	public void init() {
		this.cache = new ConcurrentHashMap<>();
		this.lastUpdate = new ConcurrentHashMap<>();
		this.lock = new Object();
	}
	
	@SuppressWarnings("unchecked")
	public List<ScheduledEvent> getUpcomingEvents() {
		synchronized(this.lock) {
			Long updated = lastUpdate.get("upcomingEvents");
			if(updated == null || updated < System.currentTimeMillis() - TIMEOUT) {
				cache.put("upcomingEvents", guildApi.getEvents(appConfig.getDiscordGuildId()));
				lastUpdate.put("upcomingEvents", System.currentTimeMillis());
			}
			return (List<ScheduledEvent>)cache.get("upcomingEvents");
		}
	}
}
