<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p>
    Thank you for your requesting a temporary password for ${TEMPLATE_SYSTEM_NAME}, 
    the marketplace for student and graduate careers.
</p>

<p>
    Our records indicate that you received a previous invitation to join 
    ${TEMPLATE_SYSTEM_NAME}, which you did not complete your registration for.
</p>

<p>
	Because of this previous invitation, we need you to complete your initial 
	registration to provide you with access. Follow the link below do to this.
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
