<#if model.projects?has_content>
	<#assign hasProjects = true />
	<#assign projects = model.projects />
<#else>
	<#assign hasProjects = false />
</#if>	

<#import "/spring.ftl" as spring />
<html>
	<head>
	</head>
   <body id="bodyId">   
		<h2>Projects</h2>			
		<ul>
		<#if hasProjects>
			<#list projects as project>
		        <li>${project.code} ${project.title} ${project.description} <button onclick="location.href='/pgadmissions/apply?project=${project.id}'">Apply now</button></li>
	      	</#list>
	    </#if>
      	</ul>
	</body>
</html>
