<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p>
    ${TEMPLATE_INITIATOR_FULL_NAME} [#if TARGET_RESOURCE_OTHER_NAME?has_content] of
    ${TARGET_RESOURCE_OTHER_NAME} [/#if]has invited your  ${TEMPLATE_RESOURCE_SCOPE}
    to join ${TEMPLATE_SYSTEM_NAME}, the marketplace for student and graduate careers.
</p>

<p>
	${TEMPLATE_SYSTEM_NAME} takes the pain out of graduate recruitment, making it easy for
	employers to directly promote opportunities to students through personal connections with
	universities and university departments. It makes it quick and easy for employers to target
	appropriate graduate hires, and helps universities to find better work experience and
	employment opportunities for their students and graduates.
</p>

[#if TEMPLATE_INVITATION_MESSAGE?has_content]
    <p>
        Personal message from ${TEMPLATE_INITIATOR_FULL_NAME}: "${TEMPLATE_INVITATION_MESSAGE}".
    </p>
[/#if]

<p>
	Follow the link below to register and join our network.
</p>

${INVITATION_ACCEPT}

<p>
	Welcome to ${TEMPLATE_SYSTEM_NAME}.
</p>

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_SIGNATORY_FULL_NAME}
</p>
