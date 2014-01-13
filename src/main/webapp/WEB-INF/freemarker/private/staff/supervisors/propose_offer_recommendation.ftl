<#import "/spring.ftl" as spring />
<input type="hidden" id="applicationId" value="${applicationForm.applicationNumber}"/>
<input type="hidden" id="approvalRoundId" name="approvalRoundId" value="<#if approvalRoundDTO.id??>${encrypter.encrypt(approvalRoundDTO.id)}</#if>" />
<section class="form-rows"  id="approvalsection">
    <h2 class="no-arrow">Assign Supervisors</h2>
    <div>
    	<form>
		<@spring.bind "approvalRoundDTO.supervisors" />
        <#if spring.status.errors.hasErrors()>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i> 
        <#else>
 		      <div class="alert alert-info"> <i class="icon-info-sign"></i> 
        </#if>
			  Assign Supervisors. You must nominate a primary and secondary supervisor and provide a description of the project and your recommended offer to the applicant. </div>
                      
<!-- Assign supervisors -->

<#assign usersInterestedInApplication = approvalRoundDTO.usersInterestedInApplicationForDisplay>
<#assign usersPotentiallyInterestedInApplication = approvalRoundDTO.usersPotentiallyInterestedInApplicationForDisplay>

<div class="row-group" id="assignSupervisorsToAppSection"> 
  <div class="row">
    <label class="plain-label" for="programSupervisors">Assign Supervisors<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.defaultSupervisors'/>"></span>
    <div class="field">
    	<select id="programSupervisors" class="list-select-from" class="max" multiple="multiple" size="8">
      		<#if usersInterestedInApplication?has_content>
		    	<optgroup id="nominated" label="Users Interested in Applicant"> 
	      			<#list usersInterestedInApplication?keys as supervisor>
	      				<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.id)}" category="nominated" <#if !usersInterestedInApplication[supervisor]> disabled="disabled" </#if>>
	      					${supervisor.firstName?html} ${supervisor.lastName?html} (${supervisor.email?html})
	      				</option>
	      			</#list>
      			</optgroup>
      		</#if>
     		<#if usersPotentiallyInterestedInApplication?has_content>
		    	<optgroup id="previous" label="Users in your Programme">
		    		<#list usersPotentiallyInterestedInApplication?keys as supervisor> 
	      				<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.id)}" category="previous" <#if !usersPotentiallyInterestedInApplication[supervisor]> disabled="disabled" </#if>>
	      					${supervisor.firstName?html} ${supervisor.lastName?html} (${supervisor.email?html})
	      				</option>
      				</#list>
     			</optgroup>
     		</#if>
      	</select>
    </div>
  </div>
  
  <!-- Available Supervisor Buttons -->
  <div class="row list-select-buttons">
    <div class="field"> <span>
      <button class="btn btn-primary" type="button" id="addSupervisorBtn"><span class="icon-down"></span> Add</button>
      <button type="button" id="removeSupervisorBtn" class="btn btn-danger"><span class="icon-up"></span> Remove</button>
      </span> </div>
  </div>
  
  <!-- Already supervisors of this application -->
  <div class="row">
    <div class="field">
      <ol id="applicationSupervisorsList">
		<#list approvalRoundDTO.supervisors as supervisor>
	        <li data-supervisorid="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.user.id)}" class="ui-widget-content">
	          ${supervisor.user.firstName?html} ${supervisor.user.lastName?html} (${supervisor.user.email?html})
	          <span style="float:right; padding-right:20px;"> <input type="radio" value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.user.id)}" name="primarySupervisor"
	          <#if supervisor.isPrimary?? && supervisor.isPrimary >
	            checked="checked"
	          </#if>
	          > Primary Supervisor </span>
	        </li>
        </#list>
      </ol>
      <@spring.bind "approvalRoundDTO.supervisors" />
      <#list spring.status.errorMessages as error> <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div></#list> </div>
  </div>
</div>

<!-- Create supervisor -->
<div class="row-group" id ="createsupervisorsection"> <#include "/private/staff/supervisors/create_supervisor_section.ftl"/> </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/supervisor/assign_supervisors.js'/>"></script>

<!-- Project Description -->
<div class="row-group" id ="projectdescriptionsection">
  <H3>Project Description</H3>
  <div class="row">
    <label id="lbl_provideProjectDescription" class="plain-label">Do you wish to provide a project description?<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectDescription'/>"></span>
    <div class="field">
      <label><input type="radio" name="provideProjectDescription" id="provideProjectDescriptionYes" value="yes" 
                        <#if  approvalRoundDTO.projectDescriptionAvailable?? && approvalRoundDTO.projectDescriptionAvailable >
        checked="checked"
        </#if>
        /> Yes</label>
      <label><input type="radio" name="provideProjectDescription" id="provideProjectDescriptionNo" value="no"
                        <#if  approvalRoundDTO.projectDescriptionAvailable?? && !approvalRoundDTO.projectDescriptionAvailable >
        checked="checked"
        </#if>
        /> No</label>
      <@spring.bind "approvalRoundDTO.projectDescriptionAvailable" />
      <#list spring.status.errorMessages as error >
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  <div class="row"> 
    <#if  !approvalRoundDTO.projectDescriptionAvailable?? || !approvalRoundDTO.projectDescriptionAvailable >
    <label for="projectTitle" id="lbl_projectTitle" class="plain-label grey-label">Project Title</label>
    <span class="hint grey" data-desc="<@spring.message 'assignSupervisor.projectTitle'/>"></span>
    <#else>
    <label for="projectTitle" id="lbl_projectTitle" class="plain-label">Project Title<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectTitle'/>"></span>
    </#if> 
    <div class="field"> <input class="max" type="text" name="projectTitle" id="projectTitle" 
                        <#if  !approvalRoundDTO.projectDescriptionAvailable?? || !approvalRoundDTO.projectDescriptionAvailable >
      disabled="disabled"
      </#if>
      value="${(approvalRoundDTO.projectTitle?html)!}" /> 
      <@spring.bind "approvalRoundDTO.projectTitle" />
    <#list spring.status.errorMessages as error >
    <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
    </#list>
      </div>
     </div>
  <div class="row"> <#if  !approvalRoundDTO.projectDescriptionAvailable?? || !approvalRoundDTO.projectDescriptionAvailable >
    <label for="projectAbstract" id="lbl_projectAbstract" class="plain-label grey-label">Project Abstract (ATAS)</label>
    <span class="hint grey" data-desc="<@spring.message 'assignSupervisor.projectAbstract'/>"></span>
    <#else>
    <label for="projectAbstract" id="lbl_projectAbstract" class="plain-label">Project Abstract (ATAS)<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectAbstract'/>"></span>
    </#if> 
    <div class="field"> <textarea class="max" cols="80" rows="6" name="projectAbstract" id="projectAbstract" <#if  !approvalRoundDTO.projectDescriptionAvailable?? || !approvalRoundDTO.projectDescriptionAvailable > disabled="disabled" </#if>
      >${(approvalRoundDTO.projectAbstract?html)!}</textarea>
      <@spring.bind "approvalRoundDTO.projectAbstract" />
    <#list spring.status.errorMessages as error >
    <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
    </#list>
    </div>
     </div>
     <#if applicationForm?? && applicationForm.project?? && !applicationForm.project.disabled>
	    <div class="row" >
	        <label id="lbl_projectAcceptingApplications" class="plain-label" for="acceptingApplications">Do you wish to continue accepting applications?<em>*</em></label>
	        <span class="hint" data-desc="<@spring.message 'prospectus.acceptingApplications'/>"></span>
	        <div class="field">
	          <input id="acceptingApplicationsRadioYes" type="radio" name="projectAcceptingApplications" value="true" <#if approvalRoundDTO.projectAcceptingApplications?? && approvalRoundDTO.projectAcceptingApplications> checked</#if>>
	          Yes
	          </input>
	          <input id="acceptingApplicationsRadioNo" type="radio" name="projectAcceptingApplications" value="false" <#if approvalRoundDTO.projectAcceptingApplications?? && !approvalRoundDTO.projectAcceptingApplications> checked</#if>>
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
      <input type="text" value="${(approvalRoundDTO.recommendedStartDate?string('dd MMM yyyy'))!}" name="offerStartDate" id="offerStartDate" class="full date" readonly>
      <@spring.bind "approvalRoundDTO.recommendedStartDate" />
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
                        <#if  approvalRoundDTO.recommendedConditionsAvailable?? && !approvalRoundDTO.recommendedConditionsAvailable >
        checked="checked"
        </#if>
        /> Unconditional</label>
      <label><input type="radio" name="offerType" id="offerTypeConditional" value="conditional"
                        <#if  approvalRoundDTO.recommendedConditionsAvailable?? && approvalRoundDTO.recommendedConditionsAvailable >
        checked="checked"
        </#if>
        /> Conditional</label>
        <@spring.bind "approvalRoundDTO.recommendedConditionsAvailable" />
    <#list spring.status.errorMessages as error >
    <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
    </#list>
    </div>
     </div>
  <div class="row"> <#if  !approvalRoundDTO.recommendedConditionsAvailable?? || !approvalRoundDTO.recommendedConditionsAvailable >
    <label id="lbl_offerConditions" class="plain-label grey-label">Recommended Conditions</label>
    <span class="hint grey" data-desc="<@spring.message 'assignSupervisor.offerConditions'/>"></span>
    <#else>
    <label id="lbl_offerConditions" class="plain-label">Recommended Conditions<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerConditions'/>"></span>
    </#if> 
    <div class="field">
      <textarea class="max" cols="80" rows="6" name="offerConditions" id="offerConditions"
      <#if !approvalRoundDTO.recommendedConditionsAvailable?? || !approvalRoundDTO.recommendedConditionsAvailable >disabled="disabled"  </#if>
      >${(approvalRoundDTO.recommendedConditions?html)!}</textarea>
      <@spring.bind "approvalRoundDTO.recommendedConditions" />
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
