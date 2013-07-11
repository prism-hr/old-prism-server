<div id="programme-details">
  <div class="rsbox">
    <div class="bs-status">
     <span class="label label-info">Status</span>
     
     <div class="icon">
      ${applicationForm.status.displayValue()}
        <span class="icon-status ${applicationForm.status.displayValue()?lower_case?replace(' ','-')}"></span>
        <#if applicationForm.nextStatus??>
          <#assign nextStatus = applicationForm.nextStatus>
          <i class="icon-chevron-right"></i>
          <span class="icon-status ${nextStatus.displayValue()?lower_case?replace(' ','-')}" data-desc="${nextStatus.displayValue()}">${nextStatus.displayValue()}</span>
        </#if>
      </div>

        <#assign actions = actionsDefinition.actions>
        <select id="actionTypeSelect" class="actionType" name="app_[${applicationForm.applicationNumber}]" data-email="${applicationForm.applicant.email?html}" data-applicationnumber="${applicationForm.applicationNumber?html}">
            <option>Actions</option>
            <#list actions as action>
              <#if action.id == "emailApplicant">
                <option value="emailApplicant" data-email="${applicationForm.applicant.email?html}" data-applicationnumber="${applicationForm.applicationNumber?html}">${action.displayName}</option>
              <#else>
                <option value="${action.id}">${action.displayName}</option>
              </#if>
            </#list>
        </select>
    
   </div>
  <div id="tools">
      <a class="btn btn-small" target="_blank" title="Click to Download" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>"><i class="icon-download-alt"></i> Download as PDF</a>
  </div>
</div>
  <div class="row">
    <div class="applicantinfo"> 
      ${applicationForm.applicationNumber} 

      ${(applicationForm.personalDetails.title?capitalize)!} ${(applicationForm.applicant.firstName?html)!} ${(applicationForm.applicant.firstName2?html)!} ${(applicationForm.applicant.firstName3?html)!} ${(applicationForm.applicant.lastName?html)!}
    </div>
  </div>
  <div class="row">
    <label>Submitted</label> 
    ${(applicationForm.submittedDate?string("dd MMM yyyy"))!"In Progress"}
    <#if (applicationForm.batchDeadline)?has_content>
        <div class="cdate">Closing date<span>${applicationForm.batchDeadline?string("dd MMM yyyy")}</span></div>
    </#if>   
  </div>
  <div class="row">
    <label>Programme</label>
      ${applicationForm.program.code} - ${applicationForm.program.title}
  </div> 
  <#if (applicationForm.projectTitle)?has_content>
  <div class="row project">
    <label>Project</label>
    <span>
        ${(applicationForm.projectTitle?html)}
     </span>
  </div>
  </#if>

</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/actions.js'/>"></script>