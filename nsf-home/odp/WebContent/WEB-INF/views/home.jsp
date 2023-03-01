<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<section class="main-content">
		<div class="home-layout">
			<section id="blog">
				<c:forEach items="${blogEntries}" var="entry">
					<t:blogEntry value="${entry}"/>
				</c:forEach>
			</section>
			<section id="recent-releases" class="activity-feed">
				<h2>Recent Releases</h2>
				
				<ol>
					<c:forEach items="${recentReleases}" var="release">
						<li>
							<a href="xsp/app/projects/${encoder.urlEncode(release.projectName)}">${fn:escapeXml(release.projectName)}</a>
							<span class="release-date"><time-ago value="${fn:escapeXml(release.releaseDate)}" /></span>
						</li>
					</c:forEach>
				</ol>
			</section>
		</div>
	</section>
</t:layout>