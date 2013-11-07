<li>                          
	<div class="box">
		<div class="title">
	    	<span class="icon-role ${role}" data-desc="${(comment.getTooltipMessage(role)?html)!}"></span>
	    	<span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span> <span class="commented">commented:</span>
	    	<span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
	  	</div>   
	  	<#if user.hasStaffRightsOnApplicationForm(applicationForm) || user.isApplicationAdministrator(applicationForm)>
	    	<p><i class="icon-bullhorn"></i> <strong>Instructions for Interviewers:</strong> <em>${(comment.furtherInterviewerDetails?html)!"Not Provided."}</em></p>
	  	</#if>
	  	<p><i class="icon-bullhorn"></i> <strong>Instructions for the Applicant:</strong> <em> ${(comment.furtherDetails?html)!"Not Provided."}</em></p>
	  
	  	<#if comment.locationUrl?? && comment.locationUrl?length &gt; 0>
	    	<p class="location"><span></span><a href="${comment.locationUrl}" target="_blank">Directions to interview</a>.</p>
	  	</#if>
	</div>
</li>