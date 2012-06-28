<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#assign avaliableOptionsSize = (programmeReviewers?size + previousReviewers?size + 4)/>
<#if (avaliableOptionsSize > 25)>
	<#assign avaliableOptionsSize = 25 />
</#if> 
<#assign selectedOptionsSize = (reviewRound.reviewers?size) + 1/>
<#if (selectedOptionsSize > 25)>
	<#assign selectedOptionsSize = 25 />
</#if> 
<div class="row">
	<span class="plain-label">Assign Reviewers<#if !user.isInRole('REVIWER')><em>*</em></#if></span>
	<span class="hint" data-desc="<@spring.message 'assignReviewer.defaultReviewers'/>"></span>
	<div class="field">
	  <select id="programReviewers" class="list-select-from" multiple="multiple" size="${avaliableOptionsSize}">
	    <optgroup id="default" label="Default reviewers">
	      <#list programmeReviewers as reviewer>
	      <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}" category="default" <#if reviewer.isReviewerInReviewRound(reviewRound)>disabled="disabled"</#if>>${reviewer.firstName?html} ${reviewer.lastName?html}</option>
	      </#list>
	    </optgroup>
	    <optgroup id="previous" label="Previous reviewers">
	     	<#list previousReviewers as reviewer>
	      		<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}" category="previous" <#if reviewer.isReviewerInReviewRound(reviewRound)>disabled="disabled"</#if>>${reviewer.firstName?html} ${reviewer.lastName?html}</option>
	      	</#list>		
	    </optgroup>
	  </select>
	</div>
 </div>
  
  <!-- Available Reviewer Buttons -->
<div class="row list-select-buttons">
	<div class="field">
	  <span>
	    <button class="blue" type="button" id="addReviewerBtn"><span class="icon-down"></span> Add</button>
	    <button type="button" id="removeReviewerBtn"><span class="icon-up"></span> Remove</button>
	  </span>
	</div>
</div>

  <!-- Already reviewers of this application -->
<div class="row">
	<div class="field">
	  <select id="applicationReviewers" class="list-select-to" multiple="multiple" <#if assignOnly?? && assignOnly> disabled="disabled"</#if> size="${selectedOptionsSize}">
		  <#list reviewRound.reviewers as reviewer>
			    <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.user.id)}">
			      ${reviewer.user.firstName?html} ${reviewer.user.lastName?html}
			    </option>
		  </#list>	  
	  </select>
	  <@spring.bind "reviewRound.reviewers" /> 
	  <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
	</div>
</div>
  
<script type="text/javascript"  src="<@spring.url '/design/default/js/reviewer/reviewers_section.js'/>"></script>
