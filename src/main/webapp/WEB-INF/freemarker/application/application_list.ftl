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
		<p>Welcome ${model.user.username}</p>	
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
		<#list model.user.roles as role>
	        <li>${role.authority}</li>
      	</#list>
      	</ul>
      	<#if model.user.isInRole('APPLICANT')>
      		<button onclick="location.href='/pgadmissions/apply'">Apply now</button>
      	</#if>
      	<br/>
      	<h3>Applications:</h3>     
      	<table> 	
		<#list model.applications as application>
		<tr>
			<td>
    			<a href="application?id=${application.id}"> ${application.id} : ${application.project.title}</a><br> 
    		</td>
    		<td>
    		  <form action="<@spring.url '/reviewer'/>">
    		      <input type="hidden" value="${application.id}" name="id"/>
        		  <#if model.user.isInRole('ADMINISTRATOR') || model.user.isInRole('REVIEWER')>
        		      <input type="submit" name="submit" value="AssignReviewer">
        		  </#if>
    		  </form>
            </td>
    		<td>
    			<form action="<@spring.url '/decision'/>" method = "POST">
    			<input type="hidden" value="${application.id}" name="id"/>
    			<#if model.user.isInRole('APPROVER')>
    				<input type="submit" name="submit" value="Approve"> 
      			</#if>
    			<#if ((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR')) )>
    				<input type="submit" name="submit" value="Reject">
      			</#if>
      			</form>
      				<form action="<@spring.url '/comment'/>" >
    			    <input type="hidden" value="${application.id}" name="id"/>
      				<#if (((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR') || model.user.isInRole('REVIEWER'))) && application.isActive() )>
    					<input type="submit" name="cmtDecision" value="Comment">
      				</#if>
      			</form>
    		</td>
    	</tr>   
		</#list>
		</table>
		
	</body>
</html>
