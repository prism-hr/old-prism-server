<html>
	<head>
    <#import "/spring.ftl" as spring />
	<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/default.css' />"/>
	<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/engine.js'/>"></script>
    <script type='text/javascript' language="javascript" src="<@spring.url '/dwr/util.js'/>"></script>
    <script type='text/javascript' language="javascript" src="<@spring.url '/dwr/interface/dwrService.js'/>"></script>
	</head>
   <body id="bodyId">   
		<h2>UCL Post-graduate admissions portal</h2>			
		<p>Welcome ${user.username}</p>	
		<input type="text" id="demoName"/>
        <input value="Send" type="button" onclick="update()"/>
        <br/>
        
        <span id="demoReply"></span>
        </script>
        <script type="text/javascript">
              function update() {
                    var name = dwr.util.getValue("demoName");
                    dwrService.sayHello(name, function(data) {
                        dwr.util.setValue("demoReply", data);
                    });
                    }
        </script>
					
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
		<#list applications as application>
    		<a href="application?id=${application.id}"> ${application.id} : ${application.project.title}</a><br>    
		</#list>
		
	</body>
</html>
