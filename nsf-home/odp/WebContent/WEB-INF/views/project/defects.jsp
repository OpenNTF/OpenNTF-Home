<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:projectLayout project="${project}" current="defects">
	<div class="lefthand-view-layout">
		<section class="activity-feed">
			<ol>
			<c:forEach items="${project.defects}" var="defect">
				<li>
					<a href="xsp/app/projects/${encoder.urlEncode(project.name)}/defects/${defect.documentId}">
						<c:out value="${empty defect.subject ? translation.noTitle : defect.subject}"/>
					</a>
					<br /><c:out value="${defect.entryAuthor}"/>
				</li>
			</c:forEach>
			</ol>
		</section>
		<section>
			<c:if test="${not empty defect}">
				<c:out value="${markdown.toHtml(defect.body)}" escapeXml="false"/>
			</c:if>
		</section>
	</div>
</t:projectLayout>