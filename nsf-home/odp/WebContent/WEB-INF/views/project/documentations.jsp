<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:projectLayout project="${project}" current="documentation">
	<div class="lefthand-view-layout">
		<section class="activity-feed">
			<ol>
			<c:forEach items="${project.documentation}" var="doc">
				<li>
					<a href="xsp/app/projects/${encoder.urlEncode(project.name)}/documentation/${doc.documentId}"><c:out value="${doc.description}"/></a>
					<br /><c:out value="${doc.entryAuthor}"/>
				</li>
			</c:forEach>
			</ol>
		</section>
		<section>
			<c:if test="${not empty doc}">
			<fieldset>
				<legend><c:out value="${doc.description}"/></legend>
				
				<p><c:out value="${temporalBean.formatDate(doc.entryDate)}"/> | <t:personName value="${doc.entryAuthor}"/></p>
				
				<ul>
				<c:forEach items="${doc.downloads}" var="download">
					<li><a href="${download.url}"><c:out value="${download.name}"/></a></li>
				</c:forEach>
				</ul>
			</fieldset>
			</c:if>
		</section>
	</div>
</t:projectLayout>