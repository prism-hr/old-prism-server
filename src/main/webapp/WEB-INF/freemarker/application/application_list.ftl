<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	<link rel="stylesheet" type="text/css"  href="<@spring.theme code='styleSheet' />"/>
	<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/engine.js'/>"></script>
    <script type='text/javascript' language="javascript" src="<@spring.url '/dwr/util.js'/>"></script>
    <script type='text/javascript' language="javascript" src="<@spring.url '/dwr/interface/acceptDWR.js'/>"></script>
	</head>
   <body id="bodyId">   
		<h2>UCL Post-graduate admissions portal</h2>			
		<p>Welcome ${model.user.username}</p>	
		
        <script type="text/javascript">
                    
              function acceptApplication(id){  
                   acceptDWR.acceptApplication(id,
                   	 	function(data) {
                        	dwr.util.setValue("demoStatus", data);
                 		},
                 		function(errorString, exception) {
                 			alert(errorString);
                 		}
                 	);    
              	
              }
        </script>
					
		<p>Roles</p>
		<ul>
		<#list model.user.roles as role>
	        <li>${role.authority}</li>
      	</#list>
      	</ul>      
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
    			<#if model.user.isInRole('APPROVER')>
    				<input value="Approve" type="button" onclick="acceptApplication(${application.id})"/>
      			</#if>
      			<span id="demoStatus"></span>
    			<#if ((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR')) )>
    				<input type="submit" name="submit" value="Reject">
      			</#if>
      			
      			<form action="<@spring.url '/comments/addComment'/>" >
    			    <input type="hidden" value="${application.id}" name="id"/>
      				<#if (((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR') || model.user.isInRole('REVIEWER'))) && application.isActive() )>
    					<input type="submit" name="cmtDecision" value="Comment">
      				</#if>
      			</form>
      			<form action="<@spring.url '/comments/showAll'/>" >
    			    <input type="hidden" value="${application.id}" name="id"/>
      				<#if (((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR') || model.user.isInRole('REVIEWER'))) && application.isActive() )>
    					<input type="submit" name="showComments" value="Show Comments">
      				</#if>
      			</form>
      			
    		</td>
    	</tr>   
		</#list>
		</table>
		<p><a href="<@spring.url '/j_spring_security_logout'/>">Log out</a></p>
	</body>
</html>
