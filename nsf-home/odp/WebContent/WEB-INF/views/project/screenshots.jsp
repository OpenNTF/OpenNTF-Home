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
<t:projectLayout project="${project}" current="screenshots">
	<script>
		function displayScreenshotLightbox(a) {
			var dialog = document.getElementById("lightbox");
			dialog.querySelector("a").href = a.href;
			dialog.querySelector("img").src = a.querySelector("img").src;
			dialog.showModal();
			return false;
		}
	</script>
	
	<c:set var="screenshots" value="${project.screenshots}"/>
	<c:choose>
		<c:when test="${empty screenshots}">
			<c:out value="${translation.screenshotsEmpty}"/>
		</c:when>
		<c:otherwise>
			<ul class="screenshots">
				<c:forEach items="${screenshots}" var="screenshot">
					<c:forEach items="${screenshot.downloads}" var="shot">
						<c:if test="${shot.contentType.startsWith('image/')}">
							<li>
								<a href="${fn:escapeXml(shot.url)}" onclick="return displayScreenshotLightbox(this)">
									<img src="${fn:escapeXml(shot.url)}" alt="${fn:escapeXml(shot.name)}"/>
								</a>
							</li>
						</c:if>
					</c:forEach>
				</c:forEach>
			</ul>
		</c:otherwise>
	</c:choose>
	
	<dialog id="lightbox" onclick="this.close()"><a><img/></a></dialog>
</t:projectLayout>