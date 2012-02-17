<html>
	<head>
    <#import "/spring.ftl" as spring />
	<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/default.css' />"/>
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
                    var name = document.getElementById('demoName').value;
                    Demo.sayHello(name, function(data) {
                        document.getElementById('demoReply').innerHTML=data;
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
			<#if user.canSee(application)>
    			<a href="application?id=${application.id}">${application.descriptionOfResearch}</a><br>    
    		</#if>	
		</#list>
		
	</body>
</html>
