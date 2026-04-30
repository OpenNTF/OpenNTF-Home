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
<t:projectLayout project="${project}" current="releases">
	<div class="lefthand-view-layout">
		<section class="activity-feed">
			
			<c:if test="${projectEditable}">
				<t:actionBar>
					<a href="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/releases/@new" class="edit-button"><c:out value="${translation.createRelease}"/></a>
				</t:actionBar>
			</c:if>
			
			<table>
				<thead>
					<tr>
						<th><c:out value="${translation.headerDate}"/></th>
						<th><c:out value="${translation.headerName}"/></th>
						<th><c:out value="${translation.headerDownloadCount}"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${project.releasesByDate}" var="listRelease">
						<tr class="${release ne null and release.documentId eq listRelease.documentId ? 'active' : ''}">
							<td><a href="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/releases/${listRelease.documentId}"><c:out value="${fn:escapeXml(temporalBean.formatDate(listRelease.releaseDate))}"/></a></td>
							<td><c:out value="${listRelease.version}"/></td>
							<td>${listRelease.downloadCount}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</section>
		<section>
			<c:if test="${not empty release}">
			<fieldset>
				<legend><c:out value="${release.version}"/></legend>
				
				<dl class="release-info">
					<dt><c:out value="${translation.releaseName}"/></dt>
					<dd><c:out value="${release.version}"/></dd>
					
					<dt><c:out value="${translation.releaseLicense}"/></dt>
					<dd><c:out value="${release.licenseType}"/></dd>
					
					<dt><c:out value="${translation.status}"/></dt>
					<dd><c:out value="${release.released ? translation.statusReleased : translation.statusNotReleased}"/></dd>
					
					<div>
						<dt><c:out value="${translation.cleared}"/></dt>
						<dd><c:out value="${release.releaseStatus}"/></dd>
					</div>
					
					<dt><c:out value="${translation.releasedOn}"/></dt>
					<dd><time-ago value="${fn:escapeXml(release.releaseDate)}"></time-ago></dd>
					
					<dt><c:out value="${translation.releaseBy}"/></dt>
					<dd>
						<c:forEach items="${release.masterChef}" var="name">
							<t:personName value="${name}"/>
						</c:forEach>
					</dd>
					
					<dt><c:out value="${translation.downloadCountLabel}"/></dt>
					<dd><c:out value="${release.downloadCount}"/></dd>
				</dl>
				<dl>
					<dt><c:out value="${translation.downloadsLabel}"/></dt>
					<dd>
						<ul>
						<c:forEach items="${release.downloads}" var="download">
							<li><a href="${mvc.basePath}/projects/${encoder.urlEncode(release.projectName)}/releases/${release.documentId}/${encoder.urlEncode(download.name)}"><c:out value="${download.name}"/></a></li>
						</c:forEach>
						</ul>
					</dd>
				</dl>
				
				<h3><c:out value="${translation.description}"/></h3>
				<p><c:out value="${markdown.toHtml(release.description)}" escapeXml="false"/></p>
				
				<c:if test="${releaseEditable}">
					<t:actionBar>
						<a href="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/releases/${release.documentId}/@edit" class="edit-button"><c:out value="${translation.editRelease}"/></a>
						<t:deleteButton action="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/releases/${release.documentId}" value="${translation.deleteRelease}" confirmation="${translation.confirmDeleteRelease}"/>
					</t:actionBar>
				</c:if>
			</fieldset>
			</c:if>
		</section>
	</div>
</t:projectLayout>