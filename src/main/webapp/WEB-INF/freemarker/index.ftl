<html>
   <body id="bodyId">
		<h2>UCL Post-graduate admissions portal</h2>		
		<p>Welcome ${user.username}</p>				
		<p>Roles</p>
		<ul>
		<#list user.roles as role>
	        <li>${role.authority}</li>
      	</#list>
      	</ul>
      	<#if user.isInRole('APPLICANT')>
      		<button onclick="location.href='/pgadmissions/apply'">Apply now</button>
      	</#if>
      	<br/>
      	<a href="/pgadmissions/applications">View applications</a>
	</body>
</html>
