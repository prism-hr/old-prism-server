<html>
	<head>
    <#import "/spring.ftl" as spring />
	<link rel="stylesheet" type="text/css"  href="<@spring.theme code='styleSheet' />"/>
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
      	<table> 	
		<#list applications as application>
		<tr>
			<td>
    			<a href="application?id=${application.id}" id= "${application.id}"> ${application.id} : ${application.project.title}</a><br> 
    		</td>
    		<td>
    			<form action="<@spring.url '/decision'/>" method = "POST">
    			<input type="hidden" value="${application.id}" name="id"/>
    			<#if user.isInRole('APPROVER')>
    				<input type="submit" name="submit" value="Approve"> 
      			</#if>
    			<#if ((user.isInRole('APPROVER') || user.isInRole('ADMINISTRATOR')) )>
    				<input type="submit" name="submit" value="Reject">
      			</#if>
      			</form>
    		</td>
    	</tr>   
		</#list>
		</table>
		
	</body>
</html>
