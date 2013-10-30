  <div class="row-group" id="assignSupervisorsToAppSection"> 
  <#assign avaliableOptionsSize = (programmeSupervisors?size + previousSupervisors?size + 4)/>
  <#if (avaliableOptionsSize > 25)>
  <#assign avaliableOptionsSize = 25 />
  </#if> 
  <#assign selectedOptionsSize = (supervisors?size) + 1/>
  <#if (selectedOptionsSize > 25)>
  <#assign selectedOptionsSize = 25 />
  </#if>
  <div class="row">
    <label class="plain-label" for="programSupervisors">Assign Supervisors<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignSupervisor.defaultSupervisors'/>"></span>
    <div class="field">
      <select id="programSupervisors" class="list-select-from" class="max" multiple="multiple" size="${avaliableOptionsSize}">
      <optgroup id="nominated" label="Applicant nominated supervisors">
        <#list nominatedSupervisors as supervisor> <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.id)}" category="nominated" <#if supervisor.isSupervisorIn(supervisors)> disabled="disabled" </#if>>
        ${supervisor.firstName?html}
        ${supervisor.lastName?html}
        </option>
        </#list>
      </optgroup>
      <optgroup id="default" label="Default supervisors">
      <#list programmeSupervisors as supervisor> 
        <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.id)}" category="default"  <#if supervisor.isSupervisorIn(supervisors)> disabled="disabled" </#if>>
        ${supervisor.firstName?html}
        ${supervisor.lastName?html}
        </option>
      </#list>
      </optgroup>
      <optgroup id="previous" label="Previous supervisors">
      <#list previousSupervisors as supervisor> 
        <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.id)}" category="previous" <#if supervisor.isSupervisorIn(supervisors)> disabled="disabled" </#if>>
        ${supervisor.firstName?html}
        ${supervisor.lastName?html}
        </option>
      </#list>
      </optgroup>
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
        <#list supervisors as supervisor>
        <li data-supervisorid="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.user.id)}" class="ui-widget-content">
          ${supervisor.user.firstName?html}
          ${supervisor.user.lastName?html}
          <span style="float:right; padding-right:20px;"> <input type="radio" value="${applicationForm.applicationNumber}|${encrypter.encrypt(supervisor.user.id)}" name="primarySupervisor"
          <#if  supervisor.isPrimary?? && supervisor.isPrimary >
            checked="checked"
          </#if>
          > Primary Supervisor </span> </li>
        </#list>
      </ol>
      <@spring.bind "${supervisorsEntityName}.supervisors" />
      <#list spring.status.errorMessages as error> <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div></#list> </div>
  </div>
</div>

<!-- Create supervisor -->
<div class="row-group" id ="createsupervisorsection"> <#include "/private/staff/supervisors/create_supervisor_section.ftl"/> </div>

<script type="text/javascript" src="<@spring.url '/design/default/js/supervisor/assign_supervisors.js'/>"></script>