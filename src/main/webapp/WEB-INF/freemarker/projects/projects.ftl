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
			<table>
				<tr>
					<td> Code </td>
					<td> Title </td>
					<td> Description </td>
				</tr>
			<#list projects as project>
				<tr id = "${project.title}" > 
					<td> ${project.code} </td>
					<td> ${project.title} </td>
					<td> ${project.description} </td>
					<td> <button id="${project.id}" name="${project.id}" onclick="location.href='/pgadmissions/apply?project=${project.id}'">Apply now</button></td>
				</tr>
	      	</#list>
	      	</table>
	    </#if>
      	</ul>
	</body>
</html>
