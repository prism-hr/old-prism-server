<h1>
    Dear ${TEMPLATE_USER_FIRST_NAME},
</h1>

<p>
    We have assessed your Application ${APPLICATION_CODE} for ${APPLICATION_PARENT_RESOURCE_TITLE} and we regret to inform you
    that it was unsucessful.
</p>
<p>
    The following explanation was given by our assessors: ${APPLICATION_REJECTION_REASON}
</p>

<#if APPLICATION_REJECTION_RECOMMEND?has_content>
	<p>${APPLICATION_REJECTION_RECOMMEND}</p>
</#if>

<p>
    We wish you success in your search for postgraduate research study.
</p>

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_AUTHOR_FULL_NAME}
</p>
