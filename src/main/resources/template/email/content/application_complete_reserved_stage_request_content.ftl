<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p><b>
${APPLICATION_CREATOR_FULL_NAME} application for ${APPLICATION_OPPORTUNITY_TYPE} in the position of:
${TEMPLATE_PARENT_RESOURCE_NAME}.
</b></p>

<p>
    The reserved stage is now due for completion. Follow the link below to advance the application to a
    different stage of consideration. You have a limited time to do this before the application will be
    rejected automatically.
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
