<div class="row-group">
    <h3 id="lbl_projectDescription">Project Description</h3>
    <div class="row">
        <label for="projectTitle" id="lbl_projectTitle" class="plain-label">Project Title<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectTitle'/>"></span>
        <div class="field">
            <input type="text" value="" id="projectTitle" name="projectTitle" class="full" />
        </div>
    </div>
    <div class="row">
        <label id="lbl_projectAbstract" class="plain-label" for="projectAbstract">Project Abstract (ATAS)<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectAbstract'/>"></span>
        <div class="field">
            <textarea id="projectAbstract" name="projectAbstract" class="max" cols="80" rows="6"></textarea>
        </div>
    </div>
    
   	<#if applicationForm?? && applicationForm.project??>
	    <div class="row" />
	        <label id="lbl_acceptingApplications" class="plain-label" for="acceptingApplications">Are you still accepting applications?<em>*</em></label>
	        <span class="hint" data-desc="<@spring.message 'prospectus.acceptingApplications'/>"></span>
	        <div class="field">
	          <input id="acceptingApplicationsRadioYes" type="radio" name="acceptingApplications" value="true" <#if applicationForm.project.advert.active> checked</#if>>
	          Yes
	          </input>
	          <input id="acceptingApplicationsRadioNo" type="radio" name="acceptingApplications" value="false" <#if !applicationForm.project.advert.active> checked</#if>>
	          No
	          </input>
	        </div>
	    </div>
    </#if>
</div>

<div class="row-group">
    <h3 id="lbl_recommendedOffer">Recommended Offer</h3>
    <div class="row">
        <label for="recommendedStartDate" id="lbl_recommendedStartDate" class="plain-label">Provisional Start Date<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerStartDate'/>"></span>
        <div class="field">
            <input type="text" value="" id="recommendedStartDate" name="recommendedStartDate" class="half date" readonly>
        </div>
    </div>
    <div class="row">
        <label id="lbl_recommendedConditionsAvailable" class="plain-label">Recommended Offer Type<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerType'/>"></span>
        <div class="field">
            <label><input type="radio" value="false" id="recommendedConditionsUnavailable" name="recommendedConditionsAvailable"> Unconditional</label>
            <label><input type="radio" value="true" id="recommendedConditionsAvailable" name="recommendedConditionsAvailable"> Conditional</label>
        </div>
    </div>
    <div class="row">
        <label for="recommendedConditions" id="lbl_recommendedConditions" class="plain-label">Recommended Conditions<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerConditions'/>"></span>
        <div class="field">
            <textarea id="recommendedConditions" name="recommendedConditions" class="max" cols="80" rows="6"></textarea>
        </div>
    </div>
</div>