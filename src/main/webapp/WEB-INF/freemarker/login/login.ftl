<html>
	<head>
    <#import "/spring.ftl" as spring />
	<link rel="stylesheet" type="text/css" href="<@spring.theme code='styleSheet' />"/>
	</head>
	<body>
		<div>
			<h2>UCL Post-graduate admissions portal</h2>
			<p>Please log in: </p>
			<form id="loginForm" method="post" class="signin" action="/pgadmissions/j_spring_security_check">
					<table cellspacing="0">
					<tr>
						<th><label for="username_or_email">Username or Email</label></th>
						<td><input id ="username_or_email" name="j_username" type="text" /></td>
					</tr>
					<tr>
						<th><labelfor="password">Password</label></th>
						<td><input id ="password" name="j_password" type="password" />				
					</tr>
					
					<tr>
						<th></th>
						<td><input name ="commit" type="submit" value="Sign In" /></td>
					</tr>
					</table>
			</form>
			<script type="text/javascript">
			document.getElementById('username_or_email').focus(); </script>
		</div>
	</body>
</html>