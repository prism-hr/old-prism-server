<html>
    <head>
    <#import "/spring.ftl" as spring />
    </head>
    <body>
        <h2>Assign reviewers:</h2>
        <h3>Application ${model.application.id}</h3>
        <form action="<@spring.url '/reviewer/reviewerSuccess'/>" method = "POST">
	        <input type="hidden" name="id" value="${model.application.id}"/>	    
	        <select name="reviewers" multiple="multiple">
	        <#list model.reviewers as reviewer>
	            <option value="${reviewer.id}">${reviewer.firstName} ${reviewer.lastName}</option>               
	        </#list>
	        </select>
	        <button type="submit">Assign</button>
        </form>
       
         <p><a href="<@spring.url '/j_spring_security_logout'/>">Log out</a></p>
    </body>
   
</html>