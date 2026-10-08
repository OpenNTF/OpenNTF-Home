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
		<t:activityFeed items="${featureRequests}" urlPart="requests" activeEntry="${doc}" project="${project}"
			titleProperty="description" query="${filterQuery}"
			showCreate="${projectEditable}" createText="${translation.createFeatureRequest}"
			createLink="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/requests/@new"/>
		<section>
			<c:if test="${not empty doc}">
				<c:if test="${docEditable}">
				<form class="doc-state-change" method="POST" action="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/requests/${doc.documentId}/@changeStatus">
					<select name="status">
						<option ${doc.status == 'Submitted' ? 'selected' : ''}><c:out value="${translation.Submitted}"/></option>
						<option ${doc.status == 'Investigating' ? 'selected' : ''}><c:out value="${translation.Investigating}"/></option>
						<option ${doc.status == 'Rejected' ? 'selected' : ''}><c:out value="${translation.Rejected}"/></option>
						<option ${doc.status == 'Added' ? 'selected' : ''}><c:out value="${translation.Added}"/></option>
					</select>
					<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
					<input type="submit" class="edit-button" value="${fn:escapeXml(translation.changeStatus)}" />
				</form>
				</c:if>
			
				<div class="comment-tree">
					<article class="comment" data-indent="0">
						<img class="avatar" alt="User avatar image" src="${usersBean[doc.entryAuthor].getCleanThumbnailUrl()}"/>
						
						<header><c:out value="${doc.description}"/></header>
						<div class="body"><c:out value="${doc.body}" escapeXml="false"/></div>
						<footer>
							<c:out value="${encoder.toCommonName(doc.entryAuthor)}"/>
							|
							<time-ago value="${fn:escapeXml(doc.entryDate)}"></time-ago>
							|
							<c:out value="${translation[doc.status]}"/>
						</footer>
					</article>
						
					<c:if test="${docEditable}">
						<t:actionBar>
							<a href="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/requests/${doc.documentId}/@edit" class="edit-button"><c:out value="${translation.editRequest}"/></a>
							<t:deleteButton action="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/requests/${doc.documentId}" value="${translation.deleteRequest}" confirmation="${translation.confirmDeleteRequest}"/>
						</t:actionBar>
					</c:if>
					
					<t:responseTree value="${responses}"/>
				</div>
			</c:if>
		</section>
	</div>
</t:projectLayout>