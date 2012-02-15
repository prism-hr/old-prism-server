<html>
   <body id="bodyId">
		<h2>Zuehlke project holding page</h2>
		<button onclick="location.href='/pgadmissions/apply'">Apply now</button>
		<h3>FreeMarker style</h3>
		<h3>User details</h3>
		<p>Username: ${user.username}</p>
				
		<p>Roles</p>
		<ul>
		<#list user.roles as role>
	        <li>${role.authority}</li>
      	</#list>
      	</ul>
	</body>
</html>
