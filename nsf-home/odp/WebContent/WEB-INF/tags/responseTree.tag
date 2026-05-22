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
<%@tag description="Displays a response tree" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@attribute name="value" required="true" type="java.lang.Object" %>
<section class="response-tree">
	<c:forEach items="${pageScope.value}" var="resp">
		<article class="comment" data-indent="${resp.indentLevel}">
			<img class="avatar" alt="User avatar image" src="${usersBean[resp.author].getCleanThumbnailUrl()}"/>
			
			<header><c:out value="${resp.subject}"/></header>
			<div class="body"><c:out value="${resp.body}" escapeXml="false"/></div>
			<footer>
				<c:out value="${encoder.toCommonName(resp.author)}"/>
				|
				<time-ago value="${fn:escapeXml(resp.date)}"></time-ago>
			</footer>
		</article>
	</c:forEach>
</section>