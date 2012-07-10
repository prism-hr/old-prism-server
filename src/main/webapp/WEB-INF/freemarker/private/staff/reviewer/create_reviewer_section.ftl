<#import "/spring.ftl" as spring />
<p><strong>Create New Reviewer</strong></p>                  

 <div class="row">
    <label class="plain-label">Reviewer First Name<em>*</em></label> 
	<span class="hint" data-desc="<@spring.message 'assignReviewer.firstName'/>"></span>
    <div class="field">
      <input class="full" type="text" name="newReviewerFirstName" id="newReviewerFirstName" value="${(reviewer.firstName?html)!}"/>
      <@spring.bind "reviewer.firstName" /> 
      <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>  
    </div>
 </div>
  
 <div class="row">
    <label class="plain-label">Reviewer Last Name<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignReviewer.lastName'/>"></span>
    <div class="field">
      <input class="full" type="text" name="newReviewerLastName" id="newReviewerLastName" value="${(reviewer.lastName?html)!}"/>                                            
      <@spring.bind "reviewer.lastName" /> 
      <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
    </div>
 </div>

 <div class="row">
    <label class="plain-label">Email<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignReviewer.email'/>"></span>
    <div class="field">
      <input class="full" type="text"  name="newReviewerEmail" id="newReviewerEmail" value="${(reviewer.email?html)!}"/>                                               
      <@spring.bind "reviewer.email" /> 
      <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
    </div>
 </div>

 <div class="row">
    <div class="field">
      <button class="blue" type="button" id="createReviewer">Add</button>
    </div>
 </div>