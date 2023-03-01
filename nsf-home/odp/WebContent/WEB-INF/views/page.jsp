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
							<turbo-frame id="page-content-${pageId}" src="xsp/app/pages/${encoder.urlEncode(pageId)}" class="tab" loading="lazy">
							</turbo-frame>
						</c:forEach>
					</div>
				</div>
			</c:if>
		</turbo-frame>
	</section>
</t:layout>