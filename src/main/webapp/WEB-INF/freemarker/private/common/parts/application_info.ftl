<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/bootstrap-select.min.css'/>"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application_list.css' />" />

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
        <#assign actions = applicationDescriptor.actionDefinitions>
        <select class="actionType selectpicker" name="app_[${applicationForm.applicationNumber}]" data-email="${applicationForm.applicant.email?html}" data-applicationnumber="${applicationForm.applicationNumber?html}">
            <option class="title">Actions</option>
            <#list actions as action>
              <#if action.action == "EMAIL_APPLICANT">
                <option value="emailApplicant" data-email="${applicationForm.applicant.email?html}" data-applicationnumber="${applicationForm.applicationNumber?html}" <#if action.raisesUrgentFlag> class="bold" data-icon="icon-bell-alt"</#if>><@spring.message 'action.${action.action}'/></option>
              <#else>
                <option value="${action.action}" <#if action.raisesUrgentFlag> class="bold" data-icon="icon-bell-alt"</#if>><@spring.message 'action.${action.action}'/></option>
              </#if>
            </#list>
        </select>
        <script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap-select.js' />"></script>
   </div>
  <div id="tools">
      <a class="btn btn-small" target="_blank" title="Click to Download" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>"><i class="icon-download-alt"></i> Download as PDF</a>
  </div>
</div>
  <div class="row">
    <div class="applicantinfo"> 
   
	  <#assign needsToSeeUrgentFlag = applicationDescriptor.needsToSeeUrgentFlag>
	  <#assign needsToSeeUpdateFlag = applicationDescriptor.needsToSeeUpdateFlag>
	  <span <#if needsToSeeUrgentFlag> data-desc="This application requires your attention" class="flagred"
	  <#elseif needsToSeeUpdateFlag> data-desc="This application has been updated" class="flagyellow"
	  <#else> data-desc="Application is progressing normally" class="flaggreen"</#if>>
  		<#if needsToSeeUrgentFlag> <i class="icon-bell-alt"></i> 
  		<#elseif needsToSeeUpdateFlag> <i class="icon-refresh"></i>
  		<#else> <i class="icon-coffee"></i> </#if>
  	   </span>
  		
      ${applicationForm.applicationNumber} 
      ${(applicationForm.personalDetails.title.displayValue)!} ${(applicationForm.applicant.firstName?html)!} ${(applicationForm.applicant.firstName2?html)!} ${(applicationForm.applicant.firstName3?html)!} ${(applicationForm.applicant.lastName?html)!}
      <#if user != applicationForm.applicant>
        <#if applicationForm.averageRatingFormatted??>
          <span class="rating icon-star" data-desc="Rating"><span>${applicationForm.averageRatingFormatted} / 5.00</span></span>
        </#if>
      </#if>
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
