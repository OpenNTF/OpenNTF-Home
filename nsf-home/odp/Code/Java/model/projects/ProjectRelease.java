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
package model.projects;

import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;
import org.openntf.xsp.jakarta.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ItemFlags;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ItemStorage;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewDocuments;
import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewEntries;

import bean.EncoderBean;
import bean.TranslationBean.Messages;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.nosql.Column;
import jakarta.nosql.Convert;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import jakarta.data.page.PageRequest;
import jakarta.data.Sort;
import jakarta.servlet.ServletContext;
import model.blog.BooleanYNConveter;

@Entity("Release")
public class ProjectRelease {
	public static final String VIEW_RELEASES = "ReleasesByDate"; //$NON-NLS-1$
	public static final String VIEW_PENDING_RELEASES = "IP Management\\Pending Releases"; //$NON-NLS-1$
	/** The admin role to be added to modified authors fields */
	public static final String ROLE_ADMIN = "[Admin]"; //$NON-NLS-1$
	
	@RepositoryProvider("projectsRepository")
	public interface Repository extends DominoRepository<ProjectRelease, String> {
		@ViewEntries(VIEW_RELEASES)
		Stream<ProjectRelease> findRecent(PageRequest pagination);
		
		@ViewDocuments(VIEW_RELEASES)
		Stream<ProjectRelease> findRecentDocuments(PageRequest pagination);

		Stream<ProjectRelease> findByProjectName(String projectName, Sort<ProjectRelease> sorts);
		
		@ViewDocuments(VIEW_PENDING_RELEASES)
		Stream<ProjectRelease> findPendingReleases();
	}
	
	public enum ReleaseStatus {
		Yes, No
	}
	
	@Id
	private String documentId;
	@Column("ProjectName")
	private String projectName;
	@Column("ReleaseNumber")
	private String version;
	@Column("ReleaseDate")
	private Temporal releaseDate;
	@Column("WhatsNew")
	@ItemStorage(type=ItemStorage.Type.MIME)
	private String description;
	@Column("DownloadsRelease")
	private int downloadCount;
	@Column("MainID")
	private String mainId;
	@Column("ReleaseInCatalog")
	private ReleaseStatus releaseStatus;
	@Column("DocAuthors")
	@ItemFlags(authors=true)
	private List<String> docAuthors;
	@Column(DominoConstants.FIELD_ATTACHMENTS)
	private List<EntityAttachment> attachments;
	@Column("LicenseType")
	private String licenseType;
	@Column("Status")
	@Convert(BooleanYNConveter.class)
	private boolean released;
	@Column("MasterChef")
	private List<String> masterChef;
	@Column(DominoConstants.FIELD_ETAG)
	private String etag;
	
	public ProjectRelease() {
	
	}
	
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Temporal getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Temporal released) {
		this.releaseDate = released;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getReleasedDate() {
		return new Date(LocalDate.from(releaseDate).toEpochDay());
	}
	
	public int getDownloadCount() {
		return downloadCount;
	}
	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}
	
	public String getMainId() {
		return mainId;
	}
	public void setMainId(String mainId) {
		this.mainId = mainId;
	}
	
	public ReleaseStatus getReleaseStatus() {
		return releaseStatus;
	}
	public void setReleaseStatus(ReleaseStatus releaseStatus) {
		this.releaseStatus = releaseStatus;
	}
	
	public List<EntityAttachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<EntityAttachment> attachments) {
		this.attachments = attachments;
	}
	
	public String getLicenseType() {
		return licenseType;
	}
	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}
	
	public boolean isReleased() {
		return released;
	}
	public void setReleased(boolean released) {
		this.released = released;
	}
	
	public List<String> getMasterChef() {
		return masterChef;
	}
	public void setMasterChef(List<String> masterChef) {
		this.masterChef = masterChef;
	}
	
	public String getEtag() {
		return etag;
	}
	public void setEtag(String etag) {
		this.etag = etag;
	}
	
	public List<Download> getDownloads() {
		EncoderBean encoder = CDI.current().select(EncoderBean.class).get();
		String contextPath = CDI.current().select(ServletContext.class).get().getContextPath();
		String unid = getDocumentId();
		return getAttachments()
			.stream()
			.map(att -> {
				Download download = new Download();
				download.setName(att.getName());
				String url = contextPath + "/0/" + unid + "/$FILE/" + encoder.urlEncode(att.getName());
				download.setUrl(url);
				return download;
			})
			.collect(Collectors.toList());
	}
	
	public void markApprovedForCatalog(boolean approved) {
		if(approved) {
			this.releaseStatus = ReleaseStatus.Yes;
			this.docAuthors = Arrays.asList(ROLE_ADMIN);
		} else {
			this.releaseStatus = ReleaseStatus.No;
		}
	}
	
	/**
	 * Retrieves a human-readable name for the project and its version.
	 * 
	 * @return a human-readable display name
	 */
	public String getDisplayName() {
		Messages translation = CDI.current().select(Messages.class, NamedLiteral.of("messages")).get();
		return translation.format("projectReleaseDisplay", getProjectName(), getVersion());
		
	}
}
