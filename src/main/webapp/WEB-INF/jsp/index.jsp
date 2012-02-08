<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<link rel="stylesheet" href="<spring:theme code='styleSheet'/>" type="text/css"/>
   </head>
   <body id="bodyId">
		<h2>Zuehlke project holding page</h2>
		<h3>JSP style</h3>
		<h3>User details</h3>
		<p>first name: ${user.firstName}</p>
		<p>last name: ${user.lastName}</p>		
		<p>Contact Numbers</p>
		<ul>
		<c:forEach var="phoneNumber" items="${user.phoneNumbers}">
	        <li>${phoneNumber.name}: ${phoneNumber.number}</li>
      	</c:forEach>
      	</ul>
		<img alt="" src="<spring:theme code='image'/>">
	</body>
</html>
