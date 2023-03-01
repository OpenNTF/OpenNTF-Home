package model.projects;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openntf.xsp.nosql.communication.driver.DominoConstants;
import org.openntf.xsp.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.nosql.mapping.extension.ItemStorage;
import org.openntf.xsp.nosql.mapping.extension.RepositoryProvider;
import org.openntf.xsp.nosql.mapping.extension.ViewEntries;
import org.openntf.xsp.nosql.mapping.extension.ViewQuery;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;
import jakarta.nosql.mapping.Pagination;
import jakarta.nosql.mapping.Sorts;

@Entity("Project")
public class Project {
	@RepositoryProvider("projectsRepository")
	public interface Repository extends DominoRepository<Project, String> {
		Optional<Project> findByProjectName(String projectName);
		
		@ViewEntries("ProjectsList")
		Stream<Project> findAll(Pagination pagination, Sorts sorts);
	}
	
	@Id
	private String id;
	@Column("ProjectName")
	private String name;
	@Column("ProjectOverview")
	private String overview;
	@Column("Details")
	@ItemStorage(type=ItemStorage.Type.MIME)
	private String details;
	@Column("DownloadsProject")
	private int downloads;
	@Column("MasterChef")
	private List<String> chefs;
	@Column("Entry_Date")
	private OffsetDateTime created;
	@Column("Last_Modified")
	private OffsetDateTime lastModified;
	@Column(DominoConstants.FIELD_CDATE)
	private OffsetDateTime docCreated;
	@Column("GitHubProject")
	private String sourceControlUrl;
	
	public Project() {
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	public int getDownloads() {
		return downloads;
	}
	public void setDownloads(int downloads) {
		this.downloads = downloads;
	}
	
	public List<String> getChefs() {
		return chefs;
	}
	public void setChefs(List<String> chefs) {
		this.chefs = chefs;
	}
	
	public OffsetDateTime getCreated() {
		return created;
	}
	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}
	
	public OffsetDateTime getLastModified() {
		return lastModified;
	}
	public void setLastModified(OffsetDateTime lastModified) {
		this.lastModified = lastModified;
	}
	
	public OffsetDateTime getDocCreated() {
		return docCreated;
	}
	
	public String getSourceControlUrl() {
		return sourceControlUrl;
	}
	
	public void setSourceControlUrl(String sourceControlUrl) {
		this.sourceControlUrl = sourceControlUrl;
	}
	
	public String getOwner() {
		List<String> chefs = getChefs();
		return chefs == null || chefs.isEmpty() ? "" : chefs.get(0);
	}
	
	public List<ProjectRelease> getReleasesByDate() {
		ProjectRelease.Repository repo = CDI.current().select(ProjectRelease.Repository.class).get();
		Stream<ProjectRelease> releases = repo.findByProjectName(getName(), Sorts.sorts().desc("releaseDate"));
		return releases.collect(Collectors.toList());
	}
	
	public List<ProjectActivity> getActivity(int entryCount) {
		ProjectActivity.Repository repo = CDI.current().select(ProjectActivity.Repository.class).get();
		ViewQuery query = ViewQuery.query().category(getName());
		return repo.findByDate(query)
			.limit(entryCount)
			.collect(Collectors.toList());
	}
	
	public List<Discussion> getDiscussionByDate() {
		Discussion.Repository repo = CDI.current().select(Discussion.Repository.class).get();
		Stream<Discussion> releases = repo.findByProjectName(getName(), Sorts.sorts().desc("entryDate"));
		return releases.collect(Collectors.toList());
	}
	
	public List<Screenshot> getScreenshots() {
		Screenshot.Repository repo = CDI.current().select(Screenshot.Repository.class).get();
		return repo.findByProjectName(getName())
			.filter(s -> !s.getAttachments().isEmpty())
			.collect(Collectors.toList());
	}
	
	public List<Documentation> getDocumentation() {
		Documentation.Repository repo = CDI.current().select(Documentation.Repository.class).get();
		return repo.findByProjectName(getName())
			.filter(s -> !s.getAttachments().isEmpty())
			.collect(Collectors.toList());
	}
	
	public List<FeatureRequest> getFeatureRequests() {
		FeatureRequest.Repository repo = CDI.current().select(FeatureRequest.Repository.class).get();
		return repo.findByProjectName(getName(), Sorts.sorts().desc("entryDate"))
			.collect(Collectors.toList());
	}
	
	public List<Defect> getDefects() {
		Defect.Repository repo = CDI.current().select(Defect.Repository.class).get();
		return repo.findByProjectName(getName(), Sorts.sorts().desc("entryDate"))
			.collect(Collectors.toList());
	}
	
	public List<Review> getReviews() {
		Review.Repository repo = CDI.current().select(Review.Repository.class).get();
		return repo.findByProjectName(getName(), Sorts.sorts().desc("entryDate"))
			.collect(Collectors.toList());
	}
}
