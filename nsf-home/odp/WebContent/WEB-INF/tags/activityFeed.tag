<%--

    Copyright (c) 2022-2024 Contributors to the OpenNTF Home App Project

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
<%@tag description="Activity feed for various project entities" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@attribute name="items" required="true" type="java.lang.Object" %>
<%@attribute name="activeEntry" required="true" type="java.lang.Object" %>
<%@attribute name="urlPart" required="true" type="java.lang.String" %>
<%@attribute name="project" required="true" type="java.lang.Object" %>
<%@attribute name="titleProperty" required="true" type="java.lang.String" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<section class="activity-feed">
	<table>
		<thead>
			<tr>
				<th><c:out value="${translation.headerDate}"/></th>
				<th><c:out value="${translation.headerName}"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${pageScope.items}" var="listEntry">
				<tr class="${pageScope.activeEntry ne null and pageScope.activeEntry.documentId eq listEntry.documentId ? 'active' : ''}">
					<td><c:out value="${fn:escapeXml(temporalBean.formatDate(listEntry.entryDate))}"/></td>
					<td>
						<a href="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/${pageScope.urlPart}/${listEntry.documentId}">
							<c:out value="${empty listEntry[pageScope.titleProperty] ? translation.noTitle : listEntry[pageScope.titleProperty]}"/>
						</a>
						<br /><c:out value="${listEntry.entryAuthor}"/>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</section>