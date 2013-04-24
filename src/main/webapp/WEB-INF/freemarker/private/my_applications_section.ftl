<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#list applications as application>
    <#assign actionsDefinition = actionDefinitions[application.applicationNumber]>
    <#assign actions = actionsDefinition.actions>
    <tr id="row_${application.applicationNumber}" name="applicationRow" class="applicationRow">
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
    <tr class="application-details" data-application-id="${application.applicationNumber}">
    	<td colspan="6">
    		<div class="application-lhs">
				<div class="alert alert-info"> <i class="icon-info-sign"></i>
					<span data-field="message"></span>
				</div>
    			<p class="applicant-name" data-field="applicant-name"></p>
    			<p class="detail-status">
    				<span class="dates">
    					<span>Submitted <span data-field="submitted-date"></span></span>
	    				<span>Last edited <span data-field="last-edited-date"></span></span>
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
    				No. of References Responded: <span data-field="references-responded"></span><br />
					<a data-field="personal-statement-link" href="javascript:void(0);">Personal Statement and CV provided</a>
    			</p>
    		</div>
    		<div class="application-rhs">
    			<a data-field="active-applications-link" href="javascript:void(0);">... Active Applications >></a>
    			<img data-field="gravatar" src="http://www.hdwallpaperspk.com/wp-content/uploads/2013/02/Tintin-cartoon-falls-foul-001-150x150.jpg" alt="Application photo" width="160" height="160" />
    			<a data-field="email" href="javascript:void(0);"></a>
    			<span data-field="phone-number" class="phone-number"></span>
    			<span data-field="skype" class="skype"></span>
    		</div>
    	</td>
	</tr>
    <tr class="placeholder"><td colspan="6">***PLACEHOLDER***</td></tr>
</#list>
