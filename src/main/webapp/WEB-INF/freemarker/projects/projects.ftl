<#if model.projects?has_content>
	<#assign hasProjects = true />
	<#assign projects = model.projects />
<#else>
	<#assign hasProjects = false />
</#if>	

<#import "/spring.ftl" as spring />
<html>
	<head>
			<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
			<script type="text/javascript">
			$(document).ready(function()
			{
			 	$('button.apply').click(function() {
			    	$('#project').val(this.id);
			    	$('#applyForm').submit();
			   });
			
			});
				
			</script>
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
					<td> <button id="${project.id}" class="apply">Apply now</button></td>
				</tr>
	      	</#list>
	      	</table>
	    </#if>
      	</ul>
      	<form id="applyForm" action="<@spring.url '/apply/new'/>" method="POST">
      		<input type="hidden" id="project" name="project" value=""/>
      	</form>
	</body>
</html>
