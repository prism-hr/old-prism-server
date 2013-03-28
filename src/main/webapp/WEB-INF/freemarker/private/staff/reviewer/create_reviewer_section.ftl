<#import "/spring.ftl" as spring />
<h3>Create New Reviewer</h3>          

 <div class="row">
    <label class="plain-label" for="newReviewerFirstName">Reviewer First Name<em>*</em></label> 
	<span class="hint" data-desc="<@spring.message 'assignReviewer.firstName'/>"></span>
    <div class="field">
      <input class="full" type="text" name="newReviewerFirstName" id="newReviewerFirstName" value="${(reviewer.firstName?html)!}"/>
      <@spring.bind "reviewer.firstName" /> 
      <#list spring.status.errorMessages as error> 
      <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
      </#list>  
    </div>
 </div>
  
 <div class="row">
    <label class="plain-label" for="newReviewerLastName">Reviewer Last Name<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignReviewer.lastName'/>"></span>
    <div class="field">
      <input class="full" type="text" name="newReviewerLastName" id="newReviewerLastName" value="${(reviewer.lastName?html)!}"/>                                            
      <@spring.bind "reviewer.lastName" /> 
      <#list spring.status.errorMessages as error> 
      <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
      </#list>
    </div>
 </div>

 <div class="row">
    <label class="plain-label" for="newReviewerEmail">Reviewer Email<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignReviewer.email'/>"></span>
    <div class="field">
      <input class="full" type="email"  name="newReviewerEmail" id="newReviewerEmail" value="${(reviewer.email?html)!}"/>                                               
      <@spring.bind "reviewer.email" /> 
      <#list spring.status.errorMessages as error>
       <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
       </#list>
    </div>
 </div>

 <div class="row">
    <div class="field">
      <button class="btn" type="button" id="createReviewer">Add</button>
    </div>
 </div>