<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<#if TEMPLATE_RECIPIENT_EMAIL?matches(APPLICATION_CREATOR_EMAIL)>
	<p><b>
		Your application for ${APPLICATION_OPPORTUNITY_TYPE} in the position of: ${TEMPLATE_PARENT_RESOURCE_NAME}.
	</b></p>
	
	<p>
	    We wish to schedule an interview with you. Please let us know when you would be available to attend.
	</p>
<#else>
	<p><b>
		${APPLICATION_CREATOR_FULL_NAME} application for ${APPLICATION_OPPORTUNITY_TYPE} in the position of: 
		${TEMPLATE_PARENT_RESOURCE_NAME}.
	</b></p>

	<p>
	    We wish to schedule an interview in connection with this application and we would like you to join 
	    the panel. Please let us know when you would be available to attend.
	</p>
</#if>

${ACTION_COMPLETE}

<p>
    Thank you in advance for your time.
</p>

<#if TEMPLATE_BUFFERED?has_content>  
    <p>${TEMPLATE_BUFFERED}</p>
</#if>

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_SIGNATORY_FULL_NAME}
</p>
