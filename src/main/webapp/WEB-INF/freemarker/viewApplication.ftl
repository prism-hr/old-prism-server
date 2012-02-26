<#import "/spring.ftl" as spring />
<html>
   <body>
		<h2>Application View</h2>	
		<p>Name: ${model.applicationForm.applicant.firstName} ${model.applicationForm.applicant.lastName}</p>
		<p>Project Title: ${model.applicationForm.project.title}</p>
		<p>Project Description: ${model.applicationForm.project.description}</p>
		<#if !model.user.isInRole('APPLICANT')>
		  <#if model.applicationForm.isUnderReview()>
		      <p>Reviewers:</p>
		      <#list model.applicationForm.reviewers as reviewer>
                    <li>${reviewer.username}</li>
              </#list>
		  <#else>
		      <p>Reviewer: Not yet assigned.</p>
		  </#if>		  
		  
		</#if>		
		<p><a href="<@spring.url '/j_spring_security_logout'/>">Log out</a></p>
	</body>
	
</html>
