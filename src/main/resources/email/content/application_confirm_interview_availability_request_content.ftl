<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<#if TEMPLATE_RECIPIENT_EMAIL?matches(APPLICATION_CREATOR_EMAIL)>
	<p><b>
		Your application for ${APPLICATION_OPPORTUNITY_TYPE} in the position of: ${TEMPLATE_PARENT_RESOURCE_NAME}.
	</b></p>
	
	<p>
	    We can confirm your arrangements for interview.
	</p>
<#else>
	<p><b>
		${APPLICATION_CREATOR_FULL_NAME} application for ${APPLICATION_OPPORTUNITY_TYPE} in the position of: 
		${TEMPLATE_PARENT_RESOURCE_NAME}.
	</b></p>

	<p>
		We can confirm your arrangements for the interview of this applicant.
	</p>
</#if>

<p>
    The interview will take place at ${APPLICATION_INTERVIEW_DATE_TIME} (${APPLICATION_INTERVIEW_TIME_ZONE}).
</p>

<#if APPLICATION_INTERVIEWEE_INSTRUCTIONS?has_content>	
	<p>Applicant instructions: ${APPLICATION_INTERVIEWEE_INSTRUCTIONS}</p>
</#if>

<#if APPLICATION_INTERVIEWER_INSTRUCTIONS?has_content>	
	<p>Interviewer instructions: ${APPLICATION_INTERVIEWER_INSTRUCTIONS}</p>
</#if>

${APPLICATION_INTERVIEW_LOCATION}

<p>
    Follow the link below to confirm that you are able to attend:
</p>

${ACTION_COMPLETE}

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_SIGNATORY_FULL_NAME}
</p>
