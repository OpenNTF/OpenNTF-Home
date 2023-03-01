<%@tag description="Individual header nav link" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="value" required="true" type="java.lang.Object" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<article class="blog-entry">
	<h2>${fn:escapeXml(pageScope.value.title)}</h2>
	<h3>
		${fn:escapeXml(encoder.toCommonName(pageScope.value.author))}
		|
		<time-ago value="${fn:escapeXml(pageScope.value.date)}" />
		|
		<a href="xsp/app/blog/${pageScope.value.unid}">${translation.comments}</a>
	</h3>
	<div>${pageScope.value.html}</div>
</article>