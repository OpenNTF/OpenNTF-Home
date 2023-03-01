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
								<a href="${shot.url}" onclick="return displayScreenshotLightbox(this)">
									<img src="${shot.url}" alt="${shot.name}"/>
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