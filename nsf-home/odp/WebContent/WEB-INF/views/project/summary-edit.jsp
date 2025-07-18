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
<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<t:projectLayout project="${project}" current="summary">
	<form method="POST" action="${mvc.basePath}/projects/${encoder.urlEncode(project.name)}/edit" class="edit-form" data-turbo="false">
		<dl>
			<dt><label for="projectName"><c:out value="${translation.projectName}"/></label></dt>
			<dd><input type="text" id="projectName" name="projectName" value="${fn:escapeXml(project.name)}"/></dd>
			
			<dt><label for="projectOverview"><c:out value="${translation.projectOverview}"/></label></dt>
			<dd><input type="text" id="projectOverview" name="projectOverview" value="${fn:escapeXml(project.overview)}"/></dd>
			
			<dt><label for="projectChefs"><c:out value="${translation.owner}"/></label></dt>
			<dd><input type="text" id="projectChefs" name="projectChefs" value="${fn:escapeXml(encoder.joinNames(project.chefs))}" multiple="multiple"/></dd>
		
			<dt><label for="projectDetails"><c:out value="${translation.projectSummary}"/></label></dt>
			<dd><textarea class="markdown-edit" id="projectDetails" name="projectDetails"><c:out value="${project.details}"/></textarea></dd>
		</dl>
		
		<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
		<p><input type="submit" value="${fn:escapeXml(translation.update)}"/></p>
	</form>
</t:projectLayout>