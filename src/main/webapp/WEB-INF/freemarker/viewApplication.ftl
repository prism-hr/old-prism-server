<html>
   <body>
		<h2>Application View</h2>	
		<p>Title: ${application.title}</p>
		<p>Gender: ${application.gender}</p>
		<p>Date of birth: ${application.dob}</p>
		<p>Country of birth: ${application.cob}</p></p>
		<p>Nationality: ${application.nat}
		<p>Description of research: ${application.descriptionOfResearch}</p>
		<#if user.isInRole('REVIEWER')>
      		<button onclick="location.href='/pgadmissions/application/review?id=${application.id}'">Review</button>
      	</#if>
	</body>
</html>
