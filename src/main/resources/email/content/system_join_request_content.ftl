<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p>
    ${TEMPLATE_INITIATOR_FULL_NAME} has requested membership of ${TEMPLATE_RESOURCE_NAME}.
</p>

<p>
	Login to accept or reject this request.
</p>

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
