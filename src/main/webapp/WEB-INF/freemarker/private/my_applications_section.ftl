<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#list applications as application>
    <#assign actionsDefinition = actionDefinitions[application.applicationNumber]>
    <#assign actions = actionsDefinition.actions>
    <tr id="row_${application.applicationNumber}" name="applicationRow" class="applicationRow" >
    	<td class="centre">
            <input type="checkbox" name="appDownload" title="<@spring.message 'myApps.toggle'/>" id="appDownload_${application.applicationNumber}" value="${application.applicationNumber}" />
    	</td>
    	<td <#if actionsDefinition.requiresAttention>data-desc="This application requires your attention" class="applicant-name flagred" <#else> class="applicant-name flaggreen"</#if>>
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
    <tr class="application-details" data-application-id="${application.applicationNumber}" data-application-status="${application.status}" data-application-issubmitted="${application.submitted?string("true", "false")}">
    	<td colspan="6">
    		<div class="application-lhs">
    		    <#if actionsDefinition.requiresAttention>
				<div class="alert alert-info"> <i class="icon-info-sign"></i>
					<span data-field="message"></span>
				</div>
				</#if>
    			<p class="applicant-name" data-field="applicant-name"></p>
    			<p class="detail-status">
    				<span class="dates">
    					<span>Submitted: <span data-field="submitted-date"></span></span>
	    				<span>Last edited: <span data-field="last-edited-date"></span></span>
    				</span>
    			</p>
    			<p class="detail-status">
    				<b>Most Recent Qualification:</b><br />
					<span data-field="most-recent-qualification"></span>
    			</p>
    			<p class="detail-status">
    				<b>Most Recent Employment:</b><br />
					<span data-field="most-recent-employment">None provided</span>
    			</p>
    			<p class="detail-status">
    				<b>Specific Funding Requirements:</b><br />
					<span data-field="funding-requirements">None provided</span>
    			</p>
    			<p class="detail-status">
    				<b>No. of References Responded:</b><br /> 
    				<span data-field="references-responded"></span><br />
    			</p>
    			<p class="detail-status">
    			     <b>Documents:</b><br />
    			     <a data-field="personal-statement-link" target="_blank" href="javascript:void(0);"></a> <br />
    			     <#if application.cv?has_content>
                     <a data-field="cv-statement-link" target="_blank" href="javascript:void(0);"></a> <br />
    			     </#if>
    			</p>
    		</div>
    		<div class="application-rhs">
    			<!--
    			<a data-field="active-applications-link" href="javascript:void(0);">... Active Applications >></a>
    			-->
    			<img data-field="gravatar" src="/pgadmissions/design/default/images/transparent.gif" width="160" height="160" />
    			<a data-field="email" href="javascript:void(0);"></a>
    			<span data-field="phone-number" class="phone-number"></span>
    			<span data-field="skype" class="skype"></span>
    		</div>
    	</td>
	</tr>
    <tr class="placeholder"><td colspan="6">***PLACEHOLDER***</td></tr>
</#list>
