<html>
	<head>
	</head>
   <body id="bodyId">   
		<h2>Projects</h2>			
		<ul>
		<#list projects as project>
	        <li>${project.code} ${project.title} ${project.description} <button onclick="location.href='/pgadmissions/apply?project=${project.id}'">Apply now</button></li>
      	</#list>
      	</ul>
	</body>
</html>
