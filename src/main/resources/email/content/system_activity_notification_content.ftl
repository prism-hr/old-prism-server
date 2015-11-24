<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p>
    We notice that you haven't logged in for a few days. Here's a quick summary of 
    what's been happening since the last time you visited us.
</p>

<#if SYSTEM_ACTIVITY_SUMMARY?has_content>
    <p>
        You currently have waiting for you:
        <br/>
        <ul>
            ${SYSTEM_ACTIVITY_SUMMARY}
        </ul>
    </p>
    
    <p>
        Follow the link below to see full details.
    </p>

    ${ACTION_COMPLETE}
</#if>

<#if SYSTEM_ADVERT_RECOMMENDATION?has_content>
    <p>
        The following new opportunities have been posted within your network:
    </p>
    
    ${SYSTEM_ADVERT_RECOMMENDATION}
    
    <p>
    	Login to browse all opportunities and employers.
    </p>
    
    ${TEMPLATE_SYSTEM_HOMEPAGE}
</#if>

<p>
	If you do not wish to receive these messages any more, or wish to take a break, 
	let us know us by logging in and updating your user preferences.
</p>

${TEMPLATE_SYSTEM_HOMEPAGE}

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_SIGNATORY_FULL_NAME}
</p>
