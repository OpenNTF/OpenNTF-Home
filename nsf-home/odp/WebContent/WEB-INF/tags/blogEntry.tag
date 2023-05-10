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
<%@tag description="Individual header nav link" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="value" required="true" type="java.lang.Object" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<article class="blog-entry">
	<h2>${fn:escapeXml(pageScope.value.title)}</h2>
	<h3>
		${fn:escapeXml(encoder.toCommonName(pageScope.value.author))}
		|
		<time-ago value="${fn:escapeXml(pageScope.value.date)}" />
		|
		<a href="${mvc.basePath}/blog/${pageScope.value.unid}">${translation.comments}</a>
	</h3>
	<div>${pageScope.value.html}</div>
</article>