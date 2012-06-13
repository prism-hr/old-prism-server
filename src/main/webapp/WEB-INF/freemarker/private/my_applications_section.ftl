<#list applications as application>
<tr id="row_${application.applicationNumber}" name="applicationRow">
	<td class="centre">
		<input type="checkbox" name="appDownload" title="Select to download" id="appDownload_${application.applicationNumber}"/>
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
    	<select class="actionType" name="app_[${application.applicationNumber}]">
		<option>Select...</option>
		<option value="view">View</option>
		<option value="print">Download</option>
		<#if user.isInRoleInProgram('APPROVER', application.program) && application.isInState('APPROVAL')>
			<option value="approve">Approve</option>
			<option value="reject">Reject</option>
			<option value="restartApprovalRequest">Request restart of approval</option>
		</#if>
		<#if  user.hasAdminRightsOnApplication(application) && application.isInState('VALIDATION')> 
			<option value="validate">Validate</option>
		</#if>
		<#if user.hasAdminRightsOnApplication(application) && application.isInState('REVIEW')> 
			<option value="validate">Evaluate reviews</option>
		</#if>
		<#if user.hasAdminRightsOnApplication(application) && application.isInState('INTERVIEW')> 
			<option value="validate">Evaluate interview feedback</option>
		</#if>
		<#if !user.isInRole('APPLICANT') && !user.isRefereeOfApplicationForm(application)>
			<option value="comment">Comment</option>								    				
		</#if>      												
		<#if (user.isReviewerInLatestReviewRoundOfApplicationForm(application)&& application.isInState('REVIEW') && user.hasRespondedToProvideReviewForApplication(application))>
			<option value="assignReviewer">Assign Reviewer</option>
		</#if>							                	   
		<#if (user.isReviewerInLatestReviewRoundOfApplicationForm(application) && application.isInState('REVIEW') && !user.hasRespondedToProvideReviewForApplicationLatestRound(application))> 
			<option value="review">Add Review</option>								    				
		</#if>      												
		<#if user.isInterviewerOfApplicationForm(application) && application.isInState('INTERVIEW') && !user.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application)> 
			<option value="interviewFeedback">Add Interview Feedback</option>								    				
		</#if>      												
		<#if (user.isRefereeOfApplicationForm(application) && application.isSubmitted() && !application.isDecided() )>
			<option value="reference">Add Reference</option>
		</#if>      												
		<#if (user.isInRole('APPLICANT') && application.isSubmitted() && !application.isDecided() && !application.isWithdrawn())>
			<option value="withdraw">Withdraw</option>
		</#if>      												
		<#if (user.hasAdminRightsOnApplication(application) && application.isInState('APPROVAL'))>
			<option value="restartApproval">Restart Approval</option>
		</#if>      												
		</select>

	</td>
	<td>
		<#if application.submittedDate??>
		${(application.submittedDate?string("dd MMM yyyy"))}
		<#else>
		<a class="proceed-link" href="/pgadmissions/application?view=view&applicationId=${application.applicationNumber}">Proceed &gt;</a>
		</#if>
	</td>
</tr>
</#list>