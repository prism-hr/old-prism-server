<h1>
    Dear ${TEMPLATE_USER_FIRST_NAME},
</h1>

<#if TEMPLATE_USER_EMAIL.equals(APPLICATION_CREATOR_EMAIL)>
	<p><b>
		Your application for ${APPLICATION_OPPORTUNITY_TYPE} at ${INSTITUTION_NAME} in the position of 
		${TEMPLATE_PARENT_RESOURCE_NAME}.
	</b></p>
	
	<p>
	    We wish to schedule an interview with you. Please let us know when you would be available to attend.
	</p>
<#else>
	<p><b>
		${APPLICATION_CREATOR_FULL_NAME} application for ${APPLICATION_OPPORTUNITY_TYPE} at ${INSTITUTION_NAME} 
		in the position of ${TEMPLATE_PARENT_RESOURCE_NAME}.
	</b></p>

	<p>
	    We wish to schedule an interview in connection with this application and we would like you to join 
	    the panel. Please let us know when you would be available to attend.
	</p>
</#if>

${ACTION_COMPLETE}

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_INVOKER_FULL_NAME}
</p>
