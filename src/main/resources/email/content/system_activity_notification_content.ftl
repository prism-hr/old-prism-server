<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>
<p>
    We notice that you haven't logged in for a few days. Here's a quick summary of
    what's been happening since the last time you visited us.
</p>

[#if SYSTEM_ACTIVITY_SUMMARY?has_content]
    <div class="section">
        <h5>You currently have waiting for you:</h5>
        <ul>
            ${SYSTEM_ACTIVITY_SUMMARY}
        </ul>
        <p>
            Follow the link to see full details. ${ACTION_COMPLETE}
        </p>
    </div>
[/#if]

[#if SYSTEM_ADVERT_RECOMMENDATION?has_content]
    <div class="section">
        <h5>Opportunities</h5>
        <p>The following new opportunities have been posted within your network:</p>

        ${SYSTEM_ADVERT_RECOMMENDATION}

        <p>
            Login to browse all opportunities and employers. ${TEMPLATE_SYSTEM_HOMEPAGE}
        </p>
    </div>

[/#if]

<p>
	If you do not wish to receive these messages any more, or wish to take a break,
	let us know us by logging in and updating your user preferences. ${TEMPLATE_SYSTEM_HOMEPAGE}
</p>

<p>
    Yours sincerely,
    <br/> ${TEMPLATE_SIGNATORY_FULL_NAME}
</p>
