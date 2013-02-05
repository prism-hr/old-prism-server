<input type="hidden" id="applicationId" value="${applicationForm.applicationNumber}"/>
<input type="hidden" id="approvalRoundId" name="approvalRoundId" value="<#if approvalRound.id??>${encrypter.encrypt(approvalRound.id)}</#if>" />  
<section class="form-rows"  id="approvalsection">
<h2 class="no-arrow">
    Assign Supervisors
</h2>

    <div>
        <div class="section-info-bar" id="add-info-bar-div">
           Assign supervisors to the application here. You may also create new supervisors.
        </div>
        
        <div class="row-group" id="assignSupervisorsToAppSection">          
            
            <#import "/spring.ftl" as spring />
            <#assign avaliableOptionsSize = (programmeSupervisors?size + previousSupervisors?size + 4)/>
            <#if (avaliableOptionsSize > 25)>
                <#assign avaliableOptionsSize = 25 />
            </#if> 
            <#assign selectedOptionsSize = (approvalRound.supervisors?size) + 1/>
            <#if (selectedOptionsSize > 25)>
                <#assign selectedOptionsSize = 25 />
            </#if> 
            <div class="row">
                <span class="plain-label">Assign Supervisors<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'assignSupervisor.defaultSupervisors'/>"></span>
                <div class="field">
                    <select id="programSupervisors" class="list-select-from" class="max" multiple="multiple" size="${avaliableOptionsSize}">
                        <optgroup id="default" label="Default supervisors">
                        <#list programmeSupervisors as supervisor>
                            <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.id)}" category="default"  <#if supervisor.isSupervisorInApprovalRound(approvalRound)> disabled="disabled" </#if>>${supervisor.firstName?html} ${supervisor.lastName?html}</option>
                        </#list>
                        </optgroup>
                        <optgroup id="previous" label="Previous supervisors">
                        <#list previousSupervisors as supervisor>
                            <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.id)}" category="previous" <#if supervisor.isSupervisorInApprovalRound(approvalRound)> disabled="disabled" </#if>>${supervisor.firstName?html} ${supervisor.lastName?html}</option>
                        </#list>
                        
                        </optgroup>
                    </select>
                </div>
            </div>
            
            
            <!-- Available Supervisor Buttons -->
            <div class="row list-select-buttons">
                <div class="field">
                    <span>
                        <button class="blue" type="button" id="addSupervisorBtn"><span class="icon-down"></span> Add</button>
                        <button type="button" id="removeSupervisorBtn"><span class="icon-up"></span> Remove</button>
                    </span>
                </div>
            </div>
            
            <!-- Already supervisors of this application -->
            <div class="row">
                <div class="field">
                    <ol id="applicationSupervisorsList">
                        <#list approvalRound.supervisors as supervisor>
                            <li data-supervisorid="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.user.id)}" class="ui-widget-content">${supervisor.user.firstName?html} ${supervisor.user.lastName?html} <span style="float:right; padding-right:20px;"><input type="radio" value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.user.id)}" name="primarySupervisor"> Primary Supervisor</span></li>
                        </#list>
                    </ol>
                </div>
            </div>
        </div>
        
        <!-- Create supervisor -->
        <div class="row-group" id ="createsupervisorsection">
            <#include "/private/staff/supervisors/create_supervisor_section.ftl"/>
        </div>
        
        <!-- Project Description --> 
        <div class="row-group" id ="projectdescriptionsection">
            <p>
                <strong>Project Description</strong>                                          
            </p>                            
            
            <div class="row">
                <label id="lbl_provideProjectDescription" class="plain-label">Do you wish to provide a project description? <em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectDescription'/>"></span>
                <div class="field">
                    <label><input type="radio" name="provideProjectDescription" id="provideProjectDescriptionYes" value="yes" /> Yes</label>
                    <label><input type="radio" name="provideProjectDescription" id="provideProjectDescriptionNo" value="no" /> No</label>
                </div>
            </div>
            
            <div class="row">
                <label id="lbl_projectTitle" class="plain-label grey-label">Project Title</label>
                <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectTitle'/>"></span>
                <div class="field">
                    <input class="max" type="text" name="projectTitle" id="projectTitle" disabled="disabled" value="" />
                </div>
            </div>
            
            <div class="row">
                <label id="lbl_projectAbstract" class="plain-label grey-label">Project Abstract</label>
                <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectAbstract'/>"></span>
                <div class="field">
                    <textarea class="max" cols="80" rows="6" name="projectAbstract" id="projectAbstract" disabled="disabled"></textarea>
                </div>
            </div>
        </div>
        
        <!-- Recommended Offer --> 
        <div class="row-group" id ="recommendedoffersection">
            <p>
                <strong>Recommended Offer</strong>                                          
            </p>                            
            
            <div class="row">
                <span class="plain-label">Provisional Start Date <em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerStartDate'/>"></span>
                <div class="field">
                    <input type="text" value="23 Sep 2013" name="offerStartDate" id="offerStartDate" class="full date" readonly="readonly">
                </div>
            </div>
            
            <div class="row">
                <label id="lbl_offerType" class="plain-label">Recommended Offer Type <em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerType'/>"></span>
                <div class="field">
                    <label><input type="radio" name="offerType" id="offerTypeUnconditional" value="unconditional" /> Unconditional</label>
                    <label><input type="radio" name="offerType" id="offerTypeConditional" value="conditional" /> Conditional</label>
                </div>
            </div>
            
            <div class="row">
                <label id="lbl_offerConditions" class="plain-label grey-label">Recommended Conditions</label>
                <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerConditions'/>"></span>
                <div class="field">
                    <textarea class="max" cols="80" rows="6" name="offerConditions" id="offerConditions" disabled="disabled"></textarea>
                </div>
            </div>
        
    </div>
       
</section>

<div id="postApprovalData"></div>

<div class="buttons">
    <button class="blue" type="button" id="assignSupervisorsBtn">Submit</button>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/supervisor/supervisor.js'/>"></script>