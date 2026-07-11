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
<t:projectLayout project="${project}" current="defects">
	<div class="lefthand-view-layout">
		<t:activityFeed items="${project.defects}" urlPart="defects" activeEntry="${defect}" project="${project}"
			titleProperty="subject"/>
		<section>
			<c:if test="${not empty defect}">
				<article class="comment" data-indent="0">
					<img class="avatar" alt="User avatar image" src="${usersBean[defect.entryAuthor].getCleanThumbnailUrl()}"/>
					
					<header><c:out value="${defect.subject}"/></header>
					<div class="body"><c:out value="${defect.body}" escapeXml="false"/></div>
					<footer>
						<c:out value="${encoder.toCommonName(defect.entryAuthor)}"/>
						|
						<time-ago value="${fn:escapeXml(defect.entryDate)}"></time-ago>
					</footer>
				</article>
			</c:if>
		</section>
	</div>
</t:projectLayout>