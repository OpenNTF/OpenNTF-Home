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
<t:projectLayout project="${project}" current="defects">
	<div class="lefthand-view-layout">
		<section class="activity-feed">
			<ol>
			<c:forEach items="${project.defects}" var="defect">
				<li>
					<a href="xsp/app/projects/${encoder.urlEncode(project.name)}/defects/${defect.documentId}">
						<c:out value="${empty defect.subject ? translation.noTitle : defect.subject}"/>
					</a>
					<br /><c:out value="${defect.entryAuthor}"/>
				</li>
			</c:forEach>
			</ol>
		</section>
		<section>
			<c:if test="${not empty defect}">
				<c:out value="${markdown.toHtml(defect.body)}" escapeXml="false"/>
			</c:if>
		</section>
	</div>
</t:projectLayout>