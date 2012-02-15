<html>
	<head>
    <#import "/spring.ftl" as spring />
	<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/default.css' />"/>
	</head>
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
      	<h3>Applications:</h3>
      	<ul>
		<#list applications as application>
			<#if user.canSee(application)>
    			<a href="application?id=${application.id}">${application.descriptionOfResearch}</a>
    		</#if>	
		</#list>
		</ul>
	</body>
</html>
