<%@tag description="Individual header nav link" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@attribute name="value" required="true" type="java.lang.String" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:if test="${not empty pageScope.value}">
	<a href="xsp/app/profiles/${encoder.urlEncode(pageScope.value)}">${fn:escapeXml(pageScope.value)}</a>
</c:if>