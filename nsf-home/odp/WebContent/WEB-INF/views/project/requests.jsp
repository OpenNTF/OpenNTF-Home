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
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<t:projectLayout project="${project}" current="requests">
	<ul class="filter-selector">
	<c:forEach items="${filters}" var="filter">
		<li ${filter.active ? 'class="active"' : '' } data-count="${filter.count}"><a href="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/requests?filter=${filter.filter}"><c:out value="${messages.format(filter.messageKey, filter.count)}"/></a></li>
	</c:forEach>
	</ul>
	<div class="lefthand-view-layout">
		<t:activityFeed items="${featureRequests}" urlPart="requests" activeEntry="${featureRequest}" project="${project}"
			titleProperty="description" query="${filterQuery}"
			showCreate="${projectEditable}" createText="${translation.createFeatureRequest}"
			createLink="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/requests/@new"/>
		<section>
			<c:if test="${not empty featureRequest}">
				<c:if test="${requestEditable}">
				<form class="doc-state-change" method="POST" action="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/requests/${featureRequest.documentId}/@changeStatus">
					<select name="status">
						<option ${featureRequest.status == 'Submitted' ? 'selected' : ''}><c:out value="${translation.submitted}"/></option>
						<option ${featureRequest.status == 'Investigating' ? 'selected' : ''}><c:out value="${translation.investigating}"/></option>
						<option ${featureRequest.status == 'Rejected' ? 'selected' : ''}><c:out value="${translation.rejected}"/></option>
						<option ${featureRequest.status == 'Added' ? 'selected' : ''}><c:out value="${translation.addedToApp}"/></option>
					</select>
					<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
					<input type="submit" value="${fn:escapeXml(translation.changeStatus)}" />
				</form>
				</c:if>
			
				<div class="comment-tree">
					<article class="comment" data-indent="0">
						<img class="avatar" alt="User avatar image" src="${usersBean[featureRequest.entryAuthor].getCleanThumbnailUrl()}"/>
						
						<header><c:out value="${featureRequest.description}"/></header>
						<div class="body"><c:out value="${featureRequest.body}" escapeXml="false"/></div>
						<footer>
							<c:out value="${encoder.toCommonName(featureRequest.entryAuthor)}"/>
							|
							<time-ago value="${fn:escapeXml(featureRequest.entryDate)}"></time-ago>
						</footer>
					</article>
					
					<t:responseTree value="${responses}"/>
				</div>
			</c:if>
		</section>
	</div>
</t:projectLayout>