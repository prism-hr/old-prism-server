  <#if model.applicationForm.isUnderReview()>
  <p>Reviewers:</p>
  <#list model.applicationForm.reviewers as reviewer>
        <li>${reviewer.firstName} ${reviewer.lastName}</li>
  </#list>
  <#else>
  <p>Reviewer: Not yet assigned.</p>
  </#if>		  
  
