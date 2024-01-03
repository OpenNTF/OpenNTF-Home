<%--

    Copyright (c) 2022-2024 Contributors to the OpenNTF Home App Project

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
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<section class="main-content">
		<p><img src="${thumbnailUrl}"/></p>
		<h1><c:out value="${displayName}"/></h1>
		
		<dl>
			<dt><c:out value="${translation.approvedContributor}"/></dt>
			<dd><c:out value="${approvedContributor}"/></dd>
			
			<dt><c:out value="${translation.projects}"/></dt>
			<dd>
				<ul>
					<c:forEach items="${projects}" var="project">
						<li><t:projectBlock value="${project}"/></li>
					</c:forEach>
				</ul>
			</dd>
		</dl>
	</section>
</t:layout>