<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#list applications as application>
    <#assign actionsDefinition = actionDefinitions[application.applicationNumber]>
    <#assign actions = actionsDefinition.actions>
    <tr id="row_${application.applicationNumber}" name="applicationRow">
    	<td class="centre">
            <input type="checkbox" name="appDownload" title="<@spring.message 'myApps.toggle'/>" id="appDownload_${application.applicationNumber}" value="${application.applicationNumber}" />
    	</td>
    	<td <#if actionsDefinition.requiresAttention>data-desc="This Application requires your attention" class="applicant-name flagred" <#else> class="applicant-name flaggreen"</#if>>
         <#if actionsDefinition.requiresAttention>
              <i class="icon-circle"></i>
            <#else>
              <i class="icon-circle-blank"></i>
            </#if>
            
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
    	
    	<select id="actionTypeSelect" class="actionType" name="app_[${application.applicationNumber}]">
            <option>Actions</option>
            <#list actions?keys as actionName>
                <option value="${actionName}">${actions[actionName]}</option>
            </#list>
        </select>
    	
    	</td>
    	<td class="centre">
    		<#if application.isWithdrawn() && !application.submittedDate??>
    			Aborted
    		<#elseif application.submittedDate??>
    			${(application.submittedDate?string("dd MMM yyyy"))}
    		<#else>
    			<a class="btn btn-success" href="/pgadmissions/application?view=view&applicationId=${application.applicationNumber}">Proceed</a>
    		</#if>
    	</td>
    </tr>
</#list>
