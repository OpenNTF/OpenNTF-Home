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
<t:layout>
	<section class="main-content">
		<c:forEach items="${blogEntrySummaries}" var="month">
			<h2>${temporalBean.toMonth(month.key)}</h2>
			<ul>
				<c:forEach items="${blogEntrySummaries[month.key]}" var="entrySummary">
					<li>
						<c:out value="${messages.format('datePrefix', temporalBean.formatDate(entrySummary.date))}"/>
						<a href="${mvc.basePath}/blog/${entrySummary.unid}">${entrySummary.viewTitle}</a>
					</li>
				</c:forEach>
			</ul>
		</c:forEach>
	</section>
</t:layout>