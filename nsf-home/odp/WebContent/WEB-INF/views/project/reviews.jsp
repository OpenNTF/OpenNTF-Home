<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:projectLayout project="${project}" current="reviews">
	<div class="lefthand-view-layout">
		<section class="activity-feed">
			<ol>
			<c:forEach items="${project.reviews}" var="review">
				<li>
					<a href="xsp/app/projects/${encoder.urlEncode(project.name)}/reviews/${review.documentId}">
						<c:out value="${empty review.subject ? translation.noTitle : review.subject}"/>
					</a>
					<br /><c:out value="${review.entryAuthor}"/>
				</li>
			</c:forEach>
			</ol>
		</section>
		<section>
			<c:if test="${not empty review}">
				<c:out value="${markdown.toHtml(review.body)}" escapeXml="false"/>
			</c:if>
		</section>
	</div>
</t:projectLayout>