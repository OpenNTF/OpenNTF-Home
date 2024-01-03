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

import java.time.OffsetDateTime;

import jakarta.json.JsonObject;
import jakarta.json.bind.annotation.JsonbProperty;

public class ScheduledEvent {
	private String id;
	@JsonbProperty("guild_id")
	private String guildId;
	@JsonbProperty("channel_id")
	private String channelId;
	@JsonbProperty("creator_id")
	private String creatorId;
	private String name;
	private String description;
	@JsonbProperty("scheduled_start_time")
	private OffsetDateTime scheduledStartTime;
	@JsonbProperty("scheduled_end_time")
	private OffsetDateTime scheduledEndTime;
	@JsonbProperty("privacy_level")
	private int privacyLevel;
	@JsonbProperty("status")
	private int status;
	@JsonbProperty("entity_type")
	private int entityType;
	@JsonbProperty("entity_id")
	private String entityId;
	@JsonbProperty("entity_metadata")
	private JsonObject entityMetadata;
	private JsonObject creator;
	@JsonbProperty("user_count")
	private int userCount;
	private String image;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGuildId() {
		return guildId;
	}
	public void setGuildId(String guildId) {
		this.guildId = guildId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public OffsetDateTime getScheduledStartTime() {
		return scheduledStartTime;
	}
	public void setScheduledStartTime(OffsetDateTime scheduledStartTime) {
		this.scheduledStartTime = scheduledStartTime;
	}
	public OffsetDateTime getScheduledEndTime() {
		return scheduledEndTime;
	}
	public void setScheduledEndTime(OffsetDateTime scheduledEndTime) {
		this.scheduledEndTime = scheduledEndTime;
	}
	public int getPrivacyLevel() {
		return privacyLevel;
	}
	public void setPrivacyLevel(int privacyLevel) {
		this.privacyLevel = privacyLevel;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getEntityType() {
		return entityType;
	}
	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public JsonObject getEntityMetadata() {
		return entityMetadata;
	}
	public void setEntityMetadata(JsonObject entityMetadata) {
		this.entityMetadata = entityMetadata;
	}
	public JsonObject getCreator() {
		return creator;
	}
	public void setCreator(JsonObject creator) {
		this.creator = creator;
	}
	public int getUserCount() {
		return userCount;
	}
	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}
