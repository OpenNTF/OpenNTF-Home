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
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<section class="main-content">
		<turbo-frame id="page-content-${page.linkId}">
			<div>
				${page.html}
			</div>
			
			<c:if test="${not empty page.childPageIds}">
				<div class="tab-container">
					<c:forEach items="${page.cleanChildPageIds}" var="pageId" varStatus="pageLoop">
						<input type="radio" id="tab${pageLoop.index}" name="tab-group" ${pageLoop.index == 0 ? 'checked="checked"' : ''} />
						<label for="tab${pageLoop.index}">${fn:escapeXml(encoder.cleanPageId(pageId))}</label>
					</c:forEach>
						
					<div class="tabs">
						<c:forEach items="${page.cleanChildPageIds}" var="pageId">
							<turbo-frame id="page-content-${pageId}" src="${mvc.basePath}/pages/${encoder.urlEncode(pageId)}" class="tab" loading="lazy">
							</turbo-frame>
						</c:forEach>
					</div>
				</div>
			</c:if>
		</turbo-frame>
	</section>
</t:layout>