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
<t:projectLayout project="${project}" current="releases">
	<fieldset>
		<legend><c:out value="${translation.release}"/></legend>
		
		<form method="POST" action="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/releases/${empty doc.documentId ? '@new' : doc.documentId}" enctype="multipart/form-data" data-turbo="false">
			<dl>
				<dt><c:out value="${translation.releaseName}"/></dt>
				<dd><input type="text" name="releaseVersion" value="${fn:escapeXml(doc.version)}" /></dd>
				
				<dt><c:out value="${translation.releaseLicense}"/></dt>
				<dd>
					<select name="releaseLicense">
						<optgroup label="${translation.permissive}">
							<option value="Apache License" ${doc.licenseType == 'Apache License' or empty doc.licenseType ? 'selected="selected"' : ''}><c:out value="${translation.licenseApache}"/></option>
						</optgroup>
						<optgroup label="${translation.copyleft}">
							<option value="GNU Affero GPL3" ${doc.licenseType == 'GNU Affero GPL3' ? 'selected="selected"' : ''}><c:out value="${translation.licenseAfferoGPL3}"/></option>
							<option value="GNU GPL3" ${doc.licenseType == 'GNU GPL3' ? 'selected="selected"' : ''}><c:out value="${translation.licenseGPL3}"/></option>
							<option value="GNU LGPL3" ${doc.licenseType == 'GNU LGPL3' ? 'selected="selected"' : ''}><c:out value="${translation.licenseLGPL3}"/></option>
						</optgroup>
					</select>
				</dd>
				
				<dt><c:out value="${translation.status}"/></dt>
				<dd>
					<label>
						<input type="radio" name="releaseReleased" value="true" ${doc.released ? 'checked="checked"' : ''} />
						<c:out value="${translation.statusReleased}"/>
					</label>
					<label>
						<input type="radio" name="releaseReleased" value="false" ${not doc.released ? 'checked="checked"' : ''} />
						<c:out value="${translation.statusNotReleased}"/>
					</label>
				</dd>
			</dl>
			<dl>
				<dt><c:out value="${translation.downloadsLabel}"/></dt>
				<dd>
					<table class="attachments">
					<tbody>
 					<c:forEach items="${doc.downloads}" var="download">
 						<tr>
 							<td><a href="${mvc.basePath}/projects/${encoder.urlEncode(doc.projectName)}/releases/${doc.documentId}/${encoder.urlEncode(download.name)}"><c:out value="${download.name}"/></a></td>
 							<td><label><input type="checkbox" name="deleteAttachments" value="${fn:escapeXml(download.name)}" /> <c:out value="${translation.deleteOnSave}"/></label></td>
 						</tr>
 					</c:forEach>
					</tbody>
					</table>
					
					<input type="file" multiple="multiple" name="releaseFiles"/>
				</dd>
			</dl>
			
			<h3><label for="releaseDescription"><c:out value="${translation.description}"/></label></h3>
			<p><textarea class="markdown-edit" id="releaseDescription" name="releaseDescription"><c:out value="${doc.descriptionMarkdown}"/></textarea></p>
			
			<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
			<p><input type="submit" value="${fn:escapeXml(translation.saveRelease)}"/></p>
		</form>
	</fieldset>
</t:projectLayout>