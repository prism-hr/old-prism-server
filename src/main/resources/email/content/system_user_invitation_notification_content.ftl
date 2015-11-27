<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p>
    ${TEMPLATE_INITIATOR_FULL_NAME} of ${TEMPLATE_RESOURCE_NAME} has invited you to join
	${TEMPLATE_SYSTEM_NAME}, the marketplace for student and graduate careers.
</p>

[#if TEMPLATE_INVITATION_MESSAGE?has_content]
    <p>
        Personal message from ${TEMPLATE_INITIATOR_FULL_NAME}: "${TEMPLATE_INVITATION_MESSAGE}".
    </p>
[/#if]

<p>
	Follow the link below to create your user profile and join our community.
</p>

${ACTION_COMPLETE}

<p>
	Welcome to ${TEMPLATE_SYSTEM_NAME}.
</p>

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_SIGNATORY_FULL_NAME}
</p>
