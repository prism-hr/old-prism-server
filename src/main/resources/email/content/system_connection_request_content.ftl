<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p>
    ${TEMPLATE_INITIATOR_FULL_NAME} of ${TARGET_RESOURCE_OTHER_NAME} has invited you to connect 
    in ${TEMPLATE_SYSTEM_NAME}, the marketplace for student and graduate careers. 
</p>

<#if TEMPLATE_INVITATION_MESSAGE?has_content>
    Personal message from ${TEMPLATE_INITIATOR_FULL_NAME}: "${TEMPLATE_INVITATION_MESSAGE}".
</#if>

<p>
	Follow the link below to accept or reject this invitation.
</p>

${ACTION_COMPLETE}

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_SIGNATORY_FULL_NAME}
</p>
