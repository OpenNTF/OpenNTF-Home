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
package model;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

import bean.EncoderBean;
import jakarta.enterprise.inject.spi.CDI;
import model.projects.Download;

/**
 * Abstract class containing common behavior for entities that provide
 * access to their attachments.
 *
 */
public abstract class AbstractAttachmentEntity {
	public abstract String getDocumentId();
	
	public abstract List<EntityAttachment> getAttachments();
	
	public abstract String getReplicaId();
	
	public List<Download> getDownloads() {
		EncoderBean encoder = CDI.current().select(EncoderBean.class).get();
		String contextPath = "/__" + getReplicaId() + ".nsf";
		String unid = getDocumentId();
		return getAttachments()
			.stream()
			.map(att -> {
				Download download = new Download();
				download.setName(att.getName());
				String url = contextPath + "/0/" + unid + "/$FILE/" + encoder.urlEncode(att.getName());
				download.setUrl(url);
				download.setContentType(att.getContentType());
				return download;
			})
			.collect(Collectors.toList());
	}
}