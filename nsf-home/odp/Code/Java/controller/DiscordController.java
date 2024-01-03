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
package controller;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.ibm.commons.util.StringUtil;

import api.external.discord.GuildApi;
import api.external.discord.ScheduledEvent;
import bean.ApplicationConfig;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

@Path("discord")
public class DiscordController {
	private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'hhmmss'Z'");
	
	@Inject
	@RestClient
	private GuildApi guildApi;
	
	@Inject
	private ApplicationConfig appConfig;
	
	@Inject
	private Models models;
	
	@GET
	@Controller
	@Produces(MediaType.TEXT_HTML)
	public String getGuild() {
		models.put("guild", guildApi.getGuild(appConfig.getDiscordGuildId()));
		return "discord.jsp";
	}
	
	@Path("events")
	@GET
	@Controller
	@Produces(MediaType.TEXT_HTML)
	public String getScheduledEvents() {
		models.put("events", guildApi.getEvents(appConfig.getDiscordGuildId()));
		return "discord/events.jsp";
	}
	
	@Path("events.ics")
	@GET
	@Produces("text/calendar")
	public String getIcs() throws ParseException {
		List<ScheduledEvent> events = guildApi.getEvents(appConfig.getDiscordGuildId());
		
		Calendar cal = new Calendar();

		for(ScheduledEvent event : events) {
			
			OffsetDateTime start = event.getScheduledStartTime();
			OffsetDateTime end = event.getScheduledEndTime();
			if(end == null) {
				end = start.plus(1, ChronoUnit.HOURS);
			}
			
			PropertyList eventProps = new PropertyList();
			eventProps.add(new DtStamp(new DateTime(start.toEpochSecond() * 1000)));
			eventProps.add(new DtStart(new DateTime(start.toEpochSecond() * 1000)));
			eventProps.add(new DtEnd(new DateTime(end.toEpochSecond() * 1000)));
			eventProps.add(new Summary(event.getName()));
			eventProps.add(new Uid(event.getId()));
			
			String desc = event.getDescription();
			if(StringUtil.isNotEmpty(desc)) {
				eventProps.add(new Description(desc));
			}
			
			cal.getComponents().add(new VEvent(eventProps));
		}
		
		return cal.toString();
	}
}
