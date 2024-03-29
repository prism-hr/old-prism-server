<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p><b>
${APPLICATION_CREATOR_FULL_NAME} application for ${APPLICATION_OPPORTUNITY_TYPE} in the position of:
${TEMPLATE_PARENT_RESOURCE_NAME}.
</b></p>

<p>
    We would appreciate your feedback on your recent interview of ${APPLICATION_CREATOR_FULL_NAME}.
</p>

<p>
    Follow the proceed link below to provide your feedback. You can also use the decline link to quickly
    decline the request if you do not feel able to comment.
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
