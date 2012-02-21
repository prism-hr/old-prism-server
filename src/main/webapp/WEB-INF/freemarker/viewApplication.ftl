<html>
   <body>
		<h2>Application View</h2>	
		<p>Name: ${application.user.firstName} ${application.user.lastName}</p>
		<p>Project Title: ${application.project.title}</p>
		<p>Project Description: ${application.project.description}</p>
		<#if user.isInRole('REVIEWER')>
      		<button onclick="location.href='/pgadmissions/application/review?id=${application.id}'">Review</button>
      	</#if>
	</body>
</html>
