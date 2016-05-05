<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p>
    Your request to create a new Program ${PROGRAM_NAME} in ${TEMPLATE_PARENT_RESOURCE_NAME} has been reviewed.
</p>

<p>
	The reviewers comments were are as follows: ${COMMENT_CONTENT}.
</p>

<p>
	${COMMENT_TRANSITION_OUTCOME}.
</p>

<p>
	In order to approve your request we would need you to provide clarification on the above. Please follow
	the link below to do so.
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
