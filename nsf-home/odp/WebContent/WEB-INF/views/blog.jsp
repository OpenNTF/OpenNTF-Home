<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout>
	<section class="main-content">
		<c:forEach items="${blogEntrySummaries}" var="month">
			<h2>${temporalBean.toMonth(month.key)}</h2>
			<ul>
				<c:forEach items="${blogEntrySummaries[month.key]}" var="entrySummary">
					<li>
						<c:out value="${messages.format('datePrefix', temporalBean.formatDate(entrySummary.date))}"/>
						<a href="xsp/app/blog/${entrySummary.unid}">${entrySummary.viewTitle}</a>
					</li>
				</c:forEach>
			</ul>
		</c:forEach>
	</section>
</t:layout>