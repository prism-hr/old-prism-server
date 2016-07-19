<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p><b>
	${APPLICATION_CREATOR_FULL_NAME} application for ${APPLICATION_OPPORTUNITY_TYPE} in the position of:
	${TEMPLATE_PARENT_RESOURCE_NAME}.
</b></p>

<p>
	We intend to make ${TEMPLATE_INITIATOR_FULL_NAME} an offer for the aforementioned position. Before we 
	can more forward with this, we require your feedback on the proposed terms of offer.
</p>

<p>
	Follow the proceed link below to provide your feedback. Be aware that we would appreciate a speedy 
	response in order to propose terms to the applicant as quickly as possible.
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
