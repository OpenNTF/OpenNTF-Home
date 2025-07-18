<%--

    Copyright (c) 2022-2025 Contributors to the OpenNTF Home App Project

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
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<t:projectLayout project="${project}" current="summary">
	<section class="project-layout">
	
		<section class="details">
			<c:out value="${markdown.toHtml(project.details)}" escapeXml="false"/>
			
			<c:if test="${editable}">
				<p><a href="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/edit"><c:out value="${translation.edit}"/></a></p>
			</c:if>
		</section>
		<section id="recent-activity" class="activity-feed">
			<ol>
				<c:forEach items="${project.getActivity(20)}" var="activity">
					<li>
						<c:choose>
							<c:when test="${activity.form eq 'release'}">
								<a href="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/releases/${activity.documentId}"><c:out value="${messages.softFormat(activity.form)}"/></a>
							</c:when>
							<c:when test="${activity.form eq 'discussion'}">
								<a href="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/discussion/${activity.documentId}"><c:out value="${messages.softFormat(activity.form)}"/></a>
							</c:when>
							<c:otherwise>
								<a href="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}"><c:out value="${messages.softFormat(activity.form)}"/></a>
							</c:otherwise>
						</c:choose>
						<span class="release-date"><c:out value="${messages.format('activityByline', activity.createdBy, activity.date)}" escapeXml="false"/></span>
					</li>
				</c:forEach>
			</ol>
		</section>
	</section>
</t:projectLayout>