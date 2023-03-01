<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:projectLayout project="${project}" current="requests">
	<div class="lefthand-view-layout">
		<section class="activity-feed">
			<ol>
			<c:forEach items="${project.featureRequests}" var="featureRequest">
				<li>
					<a href="xsp/app/projects/${encoder.urlEncode(project.name)}/requests/${featureRequest.documentId}">
						<c:out value="${empty featureRequest.subject ? translation.noTitle : featureRequest.subject}"/>
					</a>
					<br /><c:out value="${featureRequest.entryAuthor}"/>
				</li>
			</c:forEach>
			</ol>
		</section>
		<section>
			<c:if test="${not empty featureRequest}">
				<c:out value="${markdown.toHtml(featureRequest.body)}" escapeXml="false"/>
			</c:if>
		</section>
	</div>
</t:projectLayout>