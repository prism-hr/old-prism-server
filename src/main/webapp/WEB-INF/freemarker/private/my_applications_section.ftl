<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#list applications as application>
<tr id="row_${application.applicationNumber}" name="applicationRow">
	<td class="centre">
		<input type="checkbox" name="appDownload" title="<@spring.message 'myApps.toggle'/>" id="appDownload_${application.applicationNumber}" value="${application.applicationNumber}" />
	</td>
	<td class="applicant-name">
		<#if !user.isInRole('APPLICANT')>
			${application.applicant.firstName} ${application.applicant.lastName}
		</#if>
		<span class="applicant-id">${application.applicationNumber}</span>
	</td>
	<td class="program-title">${application.program.title}</td>								                
	<td class="status">
		<span class="icon-status ${application.status.displayValue()?lower_case?replace(' ','-')}" data-desc="${application.status.displayValue()}">${application.status.displayValue()}</span>
	</td>
	<td class="centre">
		  <#include "/private/common/actions.ftl"/>
	</td>
	<td class="centre">
		<#if application.isWithdrawn() && !application.submittedDate??>
			Aborted
		<#elseif application.submittedDate??>
			${(application.submittedDate?string("dd MMM yyyy"))}
		<#else>
			<a class="proceed-link" href="/pgadmissions/application?view=view&applicationId=${application.applicationNumber}">Proceed &gt;</a>
		</#if>
	</td>
</tr>
</#list>