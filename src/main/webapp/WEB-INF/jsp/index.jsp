<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<link rel="stylesheet" href="<spring:theme code='styleSheet'/>" type="text/css"/>
   </head>
   <body id="bodyId">
		<h2>Zuehlke project holding page</h2>
		<button onclick="location.href='/pgadmissions/apply'">Apply now</button>
		<h3>JSP style</h3>
		<h3>User details</h3>
		<p>Username: ${user.username}</p>
				
		<p>Roles</p>
		<ul>
		<c:forEach var="role" items="${user.roles}">
	        <li>${role.authority}</li>
      	</c:forEach>
      	</ul>
		<img alt="" src="<spring:theme code='image'/>">
	</body>
</html>
