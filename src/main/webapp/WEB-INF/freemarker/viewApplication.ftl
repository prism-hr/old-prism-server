<html>
   <body>
		<h2>Application View</h2>		
		Title: ${application.title}
		Gender: ${application.gender}
		Date of birth: ${application.dob}
		Country of birth: ${application.cob}
		Nationality: ${application.nat}
		Description of research: ${application.descriptionOfResearch}
		<#if user.isInRole('REVIEWER')>
      		<button onclick="location.href='/pgadmissions/application/review?id=${application.id}'">Review</button>
      	</#if>
	</body>
</html>
