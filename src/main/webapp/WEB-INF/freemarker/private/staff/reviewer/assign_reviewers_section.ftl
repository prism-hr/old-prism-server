<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<div class="row">
	<label class="plain-label" for="programReviewers">Assign Reviewers<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignReviewer.defaultReviewers'/>"></span>
	<div class="field">
	  <select id="programReviewers" class="list-select-from" multiple="multiple" size="8">
	    <#if usersInterestedInApplication?has_content>
	    	<optgroup id="nominated" label="Users interested in Applicant">
		      	<#list usersInterestedInApplication as reviewer>
		      		<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}" category="nominated" <#if reviewer.isReviewerInReviewRound(reviewRound)>disabled="disabled"</#if>>${reviewer.firstName?html} ${reviewer.lastName?html}</option>
		     	</#list>
	    	</optgroup>
	    </#if>
	    <#if usersPotentiallyInterestedInApplication?has_content>
		    <optgroup id="previous" label="Other users in your Programme">
		     	<#list usersPotentiallyInterestedInApplication as reviewer>
		      		<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}" category="previous" <#if reviewer.isReviewerInReviewRound(reviewRound)>disabled="disabled"</#if>>${reviewer.firstName?html} ${reviewer.lastName?html}</option>
		      	</#list>		
		    </optgroup>
		</#if>
	  </select>
	</div>
 </div>
  
  <!-- Available Reviewer Buttons -->
<div class="row list-select-buttons">
	<div class="field">
	  <span>
	    <button class="btn btn-primary" type="button" id="addReviewerBtn"><span class="icon-down"></span> Add</button>
	    <button class="btn btn-danger" type="button" id="removeReviewerBtn"><span class="icon-up"></span> Remove</button>
	  </span>
	</div>
</div>

  <!-- Already reviewers of this application -->
<div class="row">
	<div class="field">
	  <select id="applicationReviewers" class="list-select-to" multiple="multiple" size="8">
		  <#list reviewRound.reviewers as reviewer>
			    <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.user.id)}">
			      ${reviewer.user.firstName?html} ${reviewer.user.lastName?html}
			    </option>
		  </#list>	  
	  </select>
	  <@spring.bind "reviewRound.reviewers" /> 
	  <#list spring.status.errorMessages as error> 
      <div class="alert alert-error" id="reviwersErrorSpan"> <i class="icon-warning-sign"></i>
            ${error}
          </div> 
       </#list>
	</div>
</div>
  
<script type="text/javascript"  src="<@spring.url '/design/default/js/reviewer/reviewers_section.js'/>"></script>