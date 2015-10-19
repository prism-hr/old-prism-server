<h1>
    Dear ${TEMPLATE_USER_FIRST_NAME},
</h1>

<#if TEMPLATE_USER_EMAIL.equals(APPLICATION_CREATOR_EMAIL)>
	<p><b>
		Your application for ${APPLICATION_OPPORTUNITY_TYPE} at ${INSTITUTION_NAME} in the position of: 
		${TEMPLATE_PARENT_RESOURCE_NAME}.
	</b></p>
	
	<p>
	    We can confirm your arrangements for interview.
	</p>
<#else>
	<p><b>
		${APPLICATION_CREATOR_FULL_NAME} application for ${APPLICATION_OPPORTUNITY_TYPE} at ${INSTITUTION_NAME} 
		in the position of: ${TEMPLATE_PARENT_RESOURCE_NAME}.
	</b></p>

	<p>
		We can confirm your arrangements for the interview of this applicant.
	</p>
</#if>

<p>
    The interview will take place at ${APPLICATION_INTERVIEW_DATE_TIME} (${APPLICATION_INTERVIEW_TIME_ZONE}).
</p>

<#if APPLICATION_INTERVIEWER_INSTRUCTIONS??>	
	<p>Interviewer instructions: ${APPLICATION_INTERVIEWER_INSTRUCTIONS}</p>
</#if>

<#if APPLICATION_INTERVIEWEE_INSTRUCTIONS??>	
	<p>Interviewee instructions: ${APPLICATION_INTERVIEWEE_INSTRUCTIONS}</p>
</#if>

${APPLICATION_INTERVIEW_LOCATION}

${ACTION_VIEW_EDIT}

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_INVOKER_FULL_NAME}
</p>
