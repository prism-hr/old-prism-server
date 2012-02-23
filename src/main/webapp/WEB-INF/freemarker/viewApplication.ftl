<html>
   <body>
		<h2>Application View</h2>	
		<p>Name: ${application.user.firstName} ${application.user.lastName}</p>
		<p>Project Title: ${application.project.title}</p>
		<p>Project Description: ${application.project.description}</p>
		<#if !user.isInRole('APPLICANT')>
		  <#if application.isUnderReview()>
		      <p> Reviewer: ${application.reviewer.username}</p>
		  <#else>
		      <p>Reviewer: Not yet assigned.</p>
		  </#if>
		</#if>
		<p><a href="<@spring.url '/j_spring_security_logout'/>">Log out</a></p>
	</body>
	
</html>
