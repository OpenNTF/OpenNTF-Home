<%--

    Copyright (c) 2022-2023 Contributors to the OpenNTF Home App Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
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
							<a href="${mvc.basePath}/projects/${encoder.urlEncode(release.projectName)}">${fn:escapeXml(release.projectName)}</a>
							<span class="release-date"><time-ago value="${fn:escapeXml(release.releaseDate)}" /></span>
						</li>
					</c:forEach>
				</ol>
			</section>
		</div>
	</section>
</t:layout>