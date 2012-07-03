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
	<span class="hint" data-desc=""></span>
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
		<select id="applicationSupervisors" class="list-select-to" multiple="multiple" size="${selectedOptionsSize}">
			<#list approvalRound.supervisors as supervisor>
				<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.user.id)}">
					${supervisor.user.firstName?html} ${supervisor.user.lastName?html}
				</option>
			</#list>

		</select>
	</div>
</div>
<@spring.bind "approvalRound.supervisors" /> 
 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>