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