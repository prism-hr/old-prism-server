<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
</head>
<body>
	<h1>Login</h1>
	<form:form method="post"
		action="login/submit">
		<table>
			<tr>
				<td><form:label path="email">Email </form:label></td>
				<td><form:input id="email" path="email" /></td>
			</tr>
			<tr>
				<td><form:label path="password">Password </form:label></td>
				<td><form:input id = "password" path="password" /></td>
			</tr>
		</table>
		<input type="submit" id="loginBtn" value="login" />
	</form:form>
</body>
</html>