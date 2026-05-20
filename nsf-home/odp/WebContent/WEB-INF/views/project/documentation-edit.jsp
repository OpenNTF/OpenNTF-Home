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
<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<t:projectLayout project="${project}" current="documentation">
	<fieldset>
		<legend><c:out value="${translation.newDocumentation}"/></legend>
		
		<form method="POST" action="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/documentation/${empty doc.documentId ? '@new' : doc.documentId}" enctype="multipart/form-data" data-turbo="false">
			<dl>
				<dt><c:out value="${translation.documentationName}"/></dt>
				<dd><input type="text" name="docName" value="${fn:escapeXml(doc.description)}" /></dd>
				
			</dl>
			<dl>
				<dt><c:out value="${translation.downloadsLabel}"/></dt>
				<dd>
					<table class="attachments">
					<tbody>
 					<c:forEach items="${doc.downloads}" var="download">
 						<tr>
 							<td><a href="${mvc.basePath}/projects/${encoder.urlEncode(release.projectName)}/documentation/${doc.documentId}/${encoder.urlEncode(download.name)}"><c:out value="${download.name}"/></a></td>
 							<td><label><input type="checkbox" name="deleteAttachments" value="${fn:escapeXml(download.name)}" /> <c:out value="${translation.deleteOnSave}"/></label></td>
 						</tr>
 					</c:forEach>
					</tbody>
					</table>
					
					<input type="file" multiple="multiple" name="files"/>
				</dd>
			</dl>
			
			<h3><label for="docDescription"><c:out value="${translation.description}"/></label></h3>
			<p><textarea class="markdown-edit" id="docBody" name="docBody"><c:out value="${doc.bodyMarkdown}"/></textarea></p>
			
			<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
			<p><input type="submit" value="${fn:escapeXml(translation.saveDocumentation)}"/></p>
		</form>
	</fieldset>
</t:projectLayout>