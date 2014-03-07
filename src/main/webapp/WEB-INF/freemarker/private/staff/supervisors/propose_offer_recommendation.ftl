<#import "/spring.ftl" as spring />
<input type="hidden" id="applicationId" value="${applicationForm.applicationNumber}"/>
<input type="hidden" id="approvalRoundId" name="approvalRoundId" value="<#if approvalRound.id??>${encrypter.encrypt(approvalRound.id)}</#if>" />
<section class="form-rows"  id="approvalsection">
    <h2 class="no-arrow">Assign Supervisors</h2>
    <div>
    	<form>
		<@spring.bind "approvalRound.supervisors" />
        <#if spring.status.errors.hasErrors()>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i> 
        <#else>
 		      <div class="alert alert-info"> <i class="icon-info-sign"></i> 
        </#if>
			  Assign Supervisors. You must nominate a primary and secondary supervisor and provide a description of the project and your recommended offer to the applicant. </div>
                      
<!-- Assign supervisors -->
<#assign supervisors = approvalRound.supervisors>
<#assign supervisorsEntityName = 'approvalRound'>
<#include "/private/staff/supervisors/components/assign_supervisors_section.ftl"/>

<!-- Project Description -->
<div class="row-group" id ="projectdescriptionsection">
  <H3>Project Description</H3>
  <div class="row">
    <label id="lbl_provideProjectDescription" class="plain-label">Do you wish to provide a project description?<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectDescription'/>"></span>
    <div class="field">
      <label><input type="radio" name="provideProjectDescription" id="provideProjectDescriptionYes" value="yes" 
                        <#if  approvalRound.projectDescriptionAvailable?? && approvalRound.projectDescriptionAvailable >
        checked="checked"
        </#if>
        /> Yes</label>
      <label><input type="radio" name="provideProjectDescription" id="provideProjectDescriptionNo" value="no"
                        <#if  approvalRound.projectDescriptionAvailable?? && !approvalRound.projectDescriptionAvailable >
        checked="checked"
        </#if>
        /> No</label>
      <@spring.bind "approvalRound.projectDescriptionAvailable" />
      <#list spring.status.errorMessages as error >
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  <div class="row"> 
    <#if  !approvalRound.projectDescriptionAvailable?? || !approvalRound.projectDescriptionAvailable >
    <label for="projectTitle" id="lbl_projectTitle" class="plain-label grey-label">Project Title</label>
    <span class="hint grey" data-desc="<@spring.message 'assignSupervisor.projectTitle'/>"></span>
    <#else>
    <label for="projectTitle" id="lbl_projectTitle" class="plain-label">Project Title<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectTitle'/>"></span>
    </#if> 
    <div class="field"> <input class="max" type="text" name="projectTitle" id="projectTitle" 
                        <#if  !approvalRound.projectDescriptionAvailable?? || !approvalRound.projectDescriptionAvailable >
      disabled="disabled"
      </#if>
      value="${(approvalRound.projectTitle?html)!}" /> 
      <@spring.bind "approvalRound.projectTitle" />
    <#list spring.status.errorMessages as error >
    <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
    </#list>
      </div>
     </div>
  <div class="row"> <#if  !approvalRound.projectDescriptionAvailable?? || !approvalRound.projectDescriptionAvailable >
    <label for="projectAbstract" id="lbl_projectAbstract" class="plain-label grey-label">Project Abstract (ATAS)</label>
    <span class="hint grey" data-desc="<@spring.message 'assignSupervisor.projectAbstract'/>"></span>
    <#else>
    <label for="projectAbstract" id="lbl_projectAbstract" class="plain-label">Project Abstract (ATAS)<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectAbstract'/>"></span>
    </#if> 
    <div class="field"> <textarea class="max" cols="80" rows="6" name="projectAbstract" id="projectAbstract" <#if  !approvalRound.projectDescriptionAvailable?? || !approvalRound.projectDescriptionAvailable > disabled="disabled" </#if>
      >${(approvalRound.projectAbstract?html)!}</textarea>
      <@spring.bind "approvalRound.projectAbstract" />
    <#list spring.status.errorMessages as error >
    <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
    </#list>
    </div>
     </div>
     <#if applicationForm?? && applicationForm.project?? && applicationForm.project.isEnabled()>
	    <div class="row" >
	        <label id="lbl_projectAcceptingApplications" class="plain-label" for="acceptingApplications">Do you wish to continue accepting applications?<em>*</em></label>
	        <span class="hint" data-desc="<@spring.message 'prospectus.acceptingApplications'/>"></span>
	        <div class="field">
	          <input id="acceptingApplicationsRadioYes" type="radio" name="projectAcceptingApplications" value="true" <#if approvalRound.projectAcceptingApplications?? && approvalRound.projectAcceptingApplications> checked</#if>>
	          Yes
	          </input>
	          <input id="acceptingApplicationsRadioNo" type="radio" name="projectAcceptingApplications" value="false" <#if approvalRound.projectAcceptingApplications?? && !approvalRound.projectAcceptingApplications> checked</#if>>
	          No
	          </input>
	        </div>
	    </div>
    </#if>
</div>

<!-- Recommended Offer -->
<div class="row-group" id ="recommendedoffersection">
  <h3> Recommended Offer </h3>
  <div class="row"> 
  <label class="plain-label">Provisional Start Date<em>*</em></label> <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerStartDate'/>"></span>
    <div class="field">
      <input type="text" value="${(approvalRound.recommendedStartDate?string('dd MMM yyyy'))!}" name="offerStartDate" id="offerStartDate" class="full date" readonly>
      <@spring.bind "approvalRound.recommendedStartDate" />
    <#list spring.status.errorMessages as error >
   <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
    </#list>
    </div>
     </div>
  <div class="row">
    <label id="lbl_offerType" class="plain-label">Recommended Offer Type<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerType'/>"></span>
    <div class="field">
      <label><input type="radio" name="offerType" id="offerTypeUnconditional" value="unconditional"
                        <#if  approvalRound.recommendedConditionsAvailable?? && !approvalRound.recommendedConditionsAvailable >
        checked="checked"
        </#if>
        /> Unconditional</label>
      <label><input type="radio" name="offerType" id="offerTypeConditional" value="conditional"
                        <#if  approvalRound.recommendedConditionsAvailable?? && approvalRound.recommendedConditionsAvailable >
        checked="checked"
        </#if>
        /> Conditional</label>
        <@spring.bind "approvalRound.recommendedConditionsAvailable" />
    <#list spring.status.errorMessages as error >
    <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
    </#list>
    </div>
     </div>
  <div class="row"> <#if  !approvalRound.recommendedConditionsAvailable?? || !approvalRound.recommendedConditionsAvailable >
    <label id="lbl_offerConditions" class="plain-label grey-label">Recommended Conditions</label>
    <span class="hint grey" data-desc="<@spring.message 'assignSupervisor.offerConditions'/>"></span>
    <#else>
    <label id="lbl_offerConditions" class="plain-label">Recommended Conditions<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerConditions'/>"></span>
    </#if> 
    <div class="field">
      <textarea class="max" cols="80" rows="6" name="offerConditions" id="offerConditions"
      <#if  !approvalRound.recommendedConditionsAvailable?? || !approvalRound.recommendedConditionsAvailable > disabled="disabled"  </#if>
      >${(approvalRound.recommendedConditions?html)!}</textarea>
      <@spring.bind "approvalRound.recommendedConditions" />
      <#list spring.status.errorMessages as error >
      <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
      </#list> 
      </div>
    </div>
</div>
		<div class="buttons">
		  <button class="btn btn-primary" type="button" id="assignSupervisorsBtn">Submit</button>
		</div>
	</form>
</div>
</section>
<div id="postApprovalData"></div>

<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/supervisor/propose_offer_recommendation.js'/>"></script>
