<div>
	<h2>UCL Post-graduate admissions portal</h2>
	<p>Please log in: </p>
	<form id="loginForm" method="post" class="signin" action="/pgadmissions/j_spring_security_check">
			<table cellspacing="0">
			<tr>
				<th><label for="username_or_email">Username or Email</label></th>
				<td><input id ="username_or_email" name="j_username" type="text" /></td>
			</tr><br>
			<tr>
				<th><labelfor="password">Password</label></th>
				<td><input id ="password" name="j_password" type="password" />
					<small><a href="/account/resend_password">Forgot?</a></small></td>
			</tr><br>
			<tr>
				<th></th>
				<td><input id ="remember_me" name="_spring_security_remember_me"
						type="checkbox" /> 
					<label for="remember_me" class="inline">Remember me</label></td>
			</tr><br>
			<tr>
				<th></th>
				<td><input name ="commit" type="submit" value="Sign In" /></td><br>
			</tr>
			</table>
	</form>
	<script type="text/javascript">
	document.getElementById('username_or_email').focus(); </script>
</div>
