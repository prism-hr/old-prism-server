<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p><b>
	${APPLICATION_CREATOR_FULL_NAME} application for ${APPLICATION_OPPORTUNITY_TYPE} in the position of:
	${TEMPLATE_PARENT_RESOURCE_NAME}.
</b></p>

<p>
    ${TEMPLATE_INITIATOR_FULL_NAME} has stipulated that s/he would like this position to be considered as
    an on course placement. Before they can accept an offer from the provider, they need their department
    to confirm that this is acceptable from an academic standpoint.
</p>

<p>
    Follow the link below to either accept or reject this request. Be aware that both the applicant and
    the provider would appreciate a speedy response.
</p>

${ACTION_COMPLETE}

<p>
    Thank you in advance for your time.
</p>

[#if TEMPLATE_BUFFERED?has_content]
    <p>${TEMPLATE_BUFFERED}</p>
[/#if]

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_SIGNATORY_FULL_NAME}
</p>
