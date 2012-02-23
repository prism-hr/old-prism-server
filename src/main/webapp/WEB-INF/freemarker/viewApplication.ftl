<#import "/spring.ftl" as spring />
<html>
   <body>
		<h2>Application View</h2>	
		<p>Name: ${application.user.firstName} ${application.user.lastName}</p>
		<p>Project Title: ${application.project.title}</p>
		<p>Project Description: ${application.project.description}</p>
		<#if !user.isInRole('APPLICANT')>
		  <#if application.isUnderReview()>
		      <p>Reviewers:</p>
		      <#list application.reviewers as reviewer>
                    <li>${reviewer.username}</li>
              </#list>
		  <#else>
		      <p>Reviewer: Not yet assigned.</p>
		  </#if>
		</#if>
		<p><a href="<@spring.url '/j_spring_security_logout'/>">Log out</a></p>
	</body>
	
</html>
