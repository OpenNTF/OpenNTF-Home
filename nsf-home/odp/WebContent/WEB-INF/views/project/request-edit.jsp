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
<t:projectLayout project="${project}" current="requests">
	<fieldset>
		<legend><c:out value="${translation.newFeatureRequest}"/></legend>
		
		<form method="POST" action="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/requests/${empty doc.documentId ? '@new' : doc.documentId}" enctype="multipart/form-data" data-turbo="false">
			<dl>
				<dt><c:out value="${translation.featureRequestSubject}"/></dt>
				<dd><input type="text" name="subject" value="${fn:escapeXml(featureRequest.description)}" /></dd>
				
			</dl>
			
			<h3><label for="docDescription"><c:out value="${translation.description}"/></label></h3>
			<p><textarea class="markdown-edit" id="body" name="body"><c:out value="${featureRequest.bodyMarkdown}"/></textarea></p>
			
			<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
			<p><input type="submit" value="${fn:escapeXml(translation.saveFeatureRequest)}"/></p>
		</form>
	</fieldset>
</t:projectLayout>