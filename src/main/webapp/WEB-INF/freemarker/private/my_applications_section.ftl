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
    <tr class="application-details">
    	<td colspan="6">
    		<div class="application-lhs">
    			<p class="message info">
    				<i class="icon-info-sign"></i>
    				This application requires your action.
				</p>
    			<p class="applicant-name">Mr Gibril {Firstname 2} {Firstname 3} Kallon</p>
    			<p class="detail-status">
    				<span class="dates">
    					<span>Submitted 10/02/2013</span>
	    				<span>Last edited 26/03/2013</span>
    				</span>
    			</p>
    			<p class="detail-status">
    				<b>Most Recent Qualification:</b><br />
					Bachelors degree with UK honours grading scheme
					<i class="icon-circle icon-2x green"></i>
    			</p>
    			<p class="detail-status">
    				<b>Most Recent Employment:</b><br />
					None provided
					<i class="icon-circle icon-2x yellow"></i>
    			</p>
    			<p class="detail-status">
    				<b>Specific Funding Requirements:</b><br />
					None Provided
					<i class="icon-circle icon-2x red"></i>
    			</p>
    			<p class="detail-status">
    				No. of References Responded: 3<br />
					Personal Statement and CV provided
					<i class="icon-circle icon-2x green"></i>
    			</p>
    		</div>
    		<div class="application-rhs">
    			<a href="javascript:void(0);">6 Active Applications >></a>
    			<img src="http://www.hdwallpaperspk.com/wp-content/uploads/2013/02/Tintin-cartoon-falls-foul-001-150x150.jpg" alt="Application photo" width="160" height="160" />
    			<a href="mailto:useremail@domain.com">gibril.kallon@hotmail.co.uk</a>
    			<span class="phone-number">07900 000 000</span>
    			<span class="skype">kallong</span>
    		</div>
    		<div class="application-actions">
				<div class="status-wrapper">
					<div class="bs-status">
						<span class="label label-info">Status</span>
				     
						<div class="icon">
			      			Validation
			        		<span class="icon-status validation"></span>
		      			</div>				    
			   		</div>
				</div>
				<div class="review-buttons">
					<button class="btn" id="declineReview" name="declineReview" type="button" value="Submit">Decline Review</button>
					<button class="btn btn-primary" id="provideReview" name="provideReview" type="button" value="Submit">Provide Review</button>
				</div>
			</div>
    	</td>
	</tr>
    <tr class="placeholder"><td colspan="6">***PLACEHOLDER***</td></tr>
</#list>
