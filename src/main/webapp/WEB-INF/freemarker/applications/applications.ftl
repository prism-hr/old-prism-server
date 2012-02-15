<html>
	<head>
   </head>
   <body>
		<h1>${user.username}</h1>
		<#if user.isInRole('APPLICANT')>
			You are an applicant.
		<#elseif user.isInRole('REVIEWER')>
			You are a reviewer.
		<#elseif user.isInRole('RECRUITER')>
			You are a recruiter.
		</#if>
		<p>You can view the following applications</p>				

		<ul>
		<#list applications as application>
			<#if user.canSee(application)>
    			<li>${application.descriptionOfResearch}</li>
    		</#if>	
		</#list>
		</ul>
	</body>
</html>