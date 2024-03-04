<%--

    Copyright (c) 2022-2024 Contributors to the OpenNTF Home App Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@tag description="Overall Page template" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="${translation._lang}">
	<head>
		<meta http-equiv="x-ua-compatible" content="ie=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no" />
		<meta name="turbolinks-root" content="${mvc.basePath}" />
		
		<base href="${pageContext.request.contextPath}/" />
		
		<link rel="shortcut icon" href="${pageContext.request.contextPath}/img/favicon.png" />
		<link rel="apple-touch-icon" sizes="32x32" href="${pageContext.request.contextPath}/img/favicon.png" />
		<link rel="stylesheet" href="css/style.css" />
		<link rel="stylesheet" href="css/tabs.css" />
		
		<link rel="alternate" href="${mvc.basePath}/feed.xml" type="application/rss+xml" title="${fn:escapeXml(translation.feedRssBlog)}">
		<link rel="EditURI" type="application/rsd+xml" href="${mvc.basePath}/rsd.xml" />
		
		<script type="text/javascript" src="webjars/hotwired__turbo/7.1.0/dist/turbo.es2017-umd.js"></script>
		
		<script type="text/javascript" src="webjars/codemirror/5.62.2/lib/codemirror.js"></script>
		<link rel="stylesheet" href="webjars/codemirror/5.62.2/lib/codemirror.css" />
		
		<script type="text/javascript" src="customElements.js"></script>
		
		<title>${translation.appTitle}</title>
	</head>
	<body>
		<header id="pageheader">
			<a href="${mvc.basePath}/" class="logo">
				<img src="img/openntf_222222_bg.jpg" alt="${fn:escapeXml(translation.appTitle)}" />
			</a>
			<nav id="pagenav" role="navigation">
				<ul>
					<c:forEach items="${NavLinks}" var="link">
						<t:navlink value="${link}"/>
					</c:forEach>
				</ul>
			</nav>
			
			<div class="user-info">
				<c:if test="${userInfo.anonymous}">
					<a href="${pageContext.request.contextPath}?Login&RedirectTo=${encoder.urlEncode(mvc.basePath)}">Log In</a>
				</c:if>
				<c:if test="${not userInfo.anonymous}">
					<nav>
						<ul>
							<li>
								<span role="toolbar"><c:out value="${encoder.abbreviateName(userInfo.userName)}"/></span>
								<ul>
									<li><a href="${mvc.basePath}/users/@me"><c:out value="${translation.profile}"/></a></li>
									
									<li><a href="${pageContext.request.contextPath}?Logout&RedirectTo=${encoder.urlEncode(mvc.basePath)}">
										<c:out value="${translation.logoutLink}"/>
									</a></li>
								</ul>
							</li>
						</ul>
					</nav>
				</c:if>
			</div>
		</header>
		<main id="pagebody">
			<c:if test="${not empty redirectMessages}">
				<ul>
					<c:forEach items="${redirectMessages}" var="message">
						<li>${message}</li>
					</c:forEach>
				</ul>
			</c:if>
		
			<jsp:doBody />
		</main>
		<footer id="pagefooter">
			<p>${translation.copyright}</p>
		</footer>
	</body>
</html>