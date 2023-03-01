<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:projectLayout project="${project}" current="discussions">
	<div class="lefthand-view-layout">
		<section class="activity-feed">
			<ol>
			<c:forEach items="${project.discussionByDate}" var="discussion">
				<li>
					<a href="xsp/app/projects/${encoder.urlEncode(project.name)}/discussion/${discussion.documentId}"><c:out value="${discussion.subject}"/></a>
					<br /><c:out value="${discussion.entryAuthor}"/>
				</li>
			</c:forEach>
			</ol>
		</section>
		<section>
			<c:if test="${not empty discussion}">
				<c:out value="${markdown.toHtml(discussion.body)}" escapeXml="false"/>
			</c:if>
		</section>
	</div>
</t:projectLayout>