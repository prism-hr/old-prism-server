<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#list applications as application>
<tr id="row_${application.applicationForm.applicationNumber}" name="applicationRow" class="applicationRow" >
  <td class="centre"><input type="checkbox" name="appDownload" title="<@spring.message 'myApps.toggle'/>" id="appDownload_${application.applicationForm.applicationNumber}" value="${application.applicationForm.applicationNumber}" /></td>
  <td <#if application.needsToSeeUrgentFlag> data-desc="This application requires your attention" class="applicant-name flagred"
	  <#elseif application.needsToSeeUpdateFlag> data-desc="This application has been updated" class="applicant-name"
	  <#else> data-desc="Application is progressing normally" class="applicant-name flaggreen"</#if>>
  <#if application.needsToSeeUrgentFlag> <i class="icon-bell-alt"></i> 
  <#elseif application.needsToSeeUpdateFlag> <i class="icon-refresh"></i>
  <#else> <i class="icon-coffee"></i> </#if>
	${application.concatenatedApplicantfirstName}
	${application.applicantLastName}
  <span class="applicant-id">
	${application.applicationForm.applicationNumber}
  </span>
  </td>
  <td class="program-title">${application.programTitle} 
  <#if application.actualProjectTitle?has_content>
    <span class="project">
      ${(application.actualProjectTitle?html)}
    </span>
  </#if>
  </td>
  
  <td class="rating">
    <#if application.applicationForm.averageRating?? && user.id != application.applicantId>
      ${application.averageRatingFormatted}
    <#else>
      N/R
    </#if>
  </td>
  
  <td class="status">
    <span class="icon-status ${application.applicationForm.status.displayValue()?lower_case?replace(' ','-')}" data-desc="${application.applicationForm.status.displayValue()}">${application.applicationForm.status.displayValue()}</span>
    <#if application.nextStatus??>
      <#assign nextStatus = application.nextStatus>
      <i class="icon-chevron-right"></i>
      <span class="icon-status ${application.applicationForm.nextStatus.displayValue()?lower_case?replace(' ','-')}" data-desc="${application.applicationForm.nextStatus.displayValue()}">${application.applicationForm.nextStatus.displayValue()}</span>
    </#if>
  </td>
  <td class="centre">
      <select class="actionType selectpicker" name="app_[${application.applicationForm.applicationNumber?html}]" data-email="${application.applicationForm.applicant.email?html}" data-applicationnumber="${application.applicationForm.applicationNumber?html}">
        <option class="title">Actions</option>
        <#assign actions = application.actionDefinitions>
        <#list actions as action>
          <#if action.action == "EMAIL_APPLICANT">
            <option value="emailApplicant" data-email="${application.applicantEmail?html}" data-applicationnumber="${application.applicationForm.applicationNumber?html}" <#if action.raisesUrgentFlag> class="bold" data-icon="icon-bell-alt"</#if>> <@spring.message 'action.${action.action}'/> </option>
          <#else>
            <option value="${action.action}" <#if action.raisesUrgentFlag> class="bold" data-icon="icon-bell-alt"</#if>> <@spring.message 'action.${action.action}'/> </option>
          </#if>
        </#list>
    </select></td>
  <td class="centre"><#if application.applicationForm.isWithdrawn() && !application.applicationForm.submittedDate?has_content>
        Aborted
        <#elseif application.applicationForm.submittedDate?has_content>
        ${(application.applicationForm.submittedDate?string("dd MMM yyyy"))}
        <#else> <a class="btn btn-success" href="/pgadmissions/application?view=view&applicationId=${application.applicationForm.applicationNumber}">Proceed</a> </#if> </td>
</tr>
    <tr class="application-details" data-application-id="${application.applicationForm.applicationNumber}" data-application-status="${application.applicationForm.status}" data-application-issubmitted="${application.applicationForm.submitted?string("true", "false")}">
     <td colspan="7"><div class="application-lhs">
      <div class="details row-fluid">
        <div class="span2">
          <div class="thumbnail"><img  data-field="gravatar" style="height: 110px;" src="http://0.gravatar.com/avatar/42ba66a9f4503f18fb2d13bfde6c69bf?s=64&amp;d=http%3A%2F%2F0.gravatar.com%2Favatar%2Fad516503a11cd5ca435acc9bb6523536%3Fs%3D64&amp;r=G"></div>
        </div>
        <div class="span6">
          <div class="row dates">
            <div class="span6">Submitted: <b data-field="submitted-date"></b></div>
            <div class="span6">Last edited: <b data-field="last-edited-date"></b></div>
          </div>
          <div class="row tlf">
            <div class="span12"><i class="icon-envelope"></i> <a data-field="email" href="javascript:void(0);"></a></div>
          </div>
          <div class="row tlf">
            <div data-field="phone-number" class="phone-number span6"><i class="icon-phone"></i> <span ></span></div>
            <div data-field="skype" class="skype span6"><i class="icon-skype"></i> <span ></span></div>
          </div>
          <div class="row">
            <div data-field="funding-requirements" class="span6">Funding/Scholarships: <b ></b></div>
            <div data-field="references-responded" class="span6">Referees Responded: <b ></b></div>
          </div>
        </div>
        <div class="span4">
          <ul class="documents">
            <li><b>Documents:</b></li>
            <#if application.applicationForm.personalStatement?has_content>
            <li><i class="icon-file-alt"></i> <a data-field="personal-statement-link" target="_blank" href="javascript:void(0);"></a></li>
            </#if>
            <#if application.applicationForm.cv?has_content>
            <li><i class="icon-file-alt"></i> <a data-field="cv-statement-link" target="_blank" href="javascript:void(0);"></a></li>
            </#if>
          </ul>
        </div>
      </div>
      <div class="row-fluid mdetails">
            <div class="span6">Most Recent Qualification:<br/> <b data-field="most-recent-qualification"></b></div>
            <div class="span6">Most Recent Employment:<br/> <b data-field="most-recent-employment"></b></div>
       </div>
    </div>
   </td>
</tr>
    <tr class="placeholder">
  <td colspan="6">***PLACEHOLDER***</td>
</tr>
</#list>
<script>
    latestConsideredFlagIndex = ${latestConsideredFlagIndex};
</script> 