<html>
   <body>
		<h2>Application View</h2>	
		<p>Name: ${application.user.firstName} ${application.user.lastName}</p>
		<p>Project Title: ${application.project.title}</p>
		<p>Project Description: ${application.project.description}</p>
		<#if !user.isInRole('APPLICANT')>
		  <#if application.isUnderReview()>
		      <p> Reviewer: ${application.reviewer.firstName} ${application.reviewer.lastName}</p>
		  <#else>
		      <p>Reviewer: Not yet assigned.</p>
		  </#if>
		</#if>
	</body>
</html>
