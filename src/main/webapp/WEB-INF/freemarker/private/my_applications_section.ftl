<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#list applications as application>
    <#assign actionsDefinition = actionDefinitions[application.applicationNumber]>
    <#assign actions = actionsDefinition.actions>
    <tr id="row_${application.applicationNumber}" name="applicationRow" class="applicationRow" >
  <td class="centre"><input type="checkbox" name="appDownload" title="<@spring.message 'myApps.toggle'/>" id="appDownload_${application.applicationNumber}" value="${application.applicationNumber}" /></td>
  <td <#if actionsDefinition.requiresAttention>data-desc="This application requires your attention" class="applicant-name flagred" <#else> class="applicant-name flaggreen"</#if>>
  <#if actionsDefinition.requiresAttention> <i class="icon-circle"></i> <#else> <i class="icon-circle-blank"></i> </#if>
  
  <#if !user.isInRole('APPLICANT')>
  ${application.applicant.firstName}
  ${application.applicant.lastName}
  </#if> <span class="applicant-id">
      ${application.applicationNumber}
      </span>
  </td>
  <td class="program-title">${application.program.title}</td>
  <td class="status"><span class="icon-status ${application.status.displayValue()?lower_case?replace(' ','-')}" data-desc="${application.status.displayValue()}">
    ${application.status.displayValue()}
    </span></td>
  <td class="centre"><select id="actionTypeSelect" class="actionType" name="app_[${application.applicationNumber}]">
      <option>Actions</option>
      <#list actions?keys as actionName>
      <option value="${actionName}">
      ${actions[actionName]}
      </option>
      </#list>
    </select></td>
  <td class="centre"><#if application.isWithdrawn() && !application.submittedDate??>
        Aborted
        <#elseif application.submittedDate??>
        ${(application.submittedDate?string("dd MMM yyyy"))}
        <#else> <a class="btn btn-success" href="/pgadmissions/application?view=view&applicationId=${application.applicationNumber}">Proceed</a> </#if> </td>
</tr>
    <tr class="application-details" data-application-id="${application.applicationNumber}" data-application-status="${application.status}" data-application-issubmitted="${application.submitted?string("true", "false")}">
  <td colspan="6"><div class="application-lhs">
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
            <#if application.personalStatement?has_content>
            <li><i class="icon-file-alt"></i> <a data-field="personal-statement-link" target="_blank" href="javascript:void(0);"></a></li>
            </#if>
            <#if application.cv?has_content>
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