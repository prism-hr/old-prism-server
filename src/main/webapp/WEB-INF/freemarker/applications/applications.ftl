<html>
	<head>
   </head>
   <body>
		<h1> Welcome ${user.username}! </h1>
		<#if user.isInRole('APPLICANT')>
			You are an applicant.
		<#elseif user.isInRole('REVIEWER')>
			You are a reviewer.
		<#elseif user.isInRole('RECRUITER')>
			You are a recruiter.
		</#if>		
	</body>
</html>