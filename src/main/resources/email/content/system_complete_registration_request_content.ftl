<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p>
    Thank you for your registration for ${TEMPLATE_SYSTEM_NAME}, the marketplace for student and graduate careers.
</p>

<p>
	Please follow the link below to confirm your identity. You will be asked to login using your account credentials.
</p>

${SYSTEM_USER_ACCOUNT_ACTIVATION}

<p>
	Welcome to ${TEMPLATE_SYSTEM_NAME}.
</p>

<#if TEMPLATE_BUFFERED?has_content>  
    <p>${TEMPLATE_BUFFERED}</p>
</#if>

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_SIGNATORY_FULL_NAME}
</p>
