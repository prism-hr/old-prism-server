<h1>
    Dear ${TEMPLATE_RECIPIENT_FIRST_NAME},
</h1>

<p><b>
	Your application for ${APPLICATION_OPPORTUNITY_TYPE} in the position of: ${TEMPLATE_PARENT_RESOURCE_NAME}.
</b></p>

<p>
    We have completed our assessment and we are pleased to inform you that you have been successful.
</p>

<p>
    We now need you to login at your earliest convenience to confirm that you are happy to accept our 
    terms of offer. <b>Please do this quickly, so that we have the opportunity to offer the opportunity to 
    another applicant should you wish to decline.</b>
</p>

<p>
	Our terms of offer are as follows:
</p>

<ul>
	<li><b>Position:</b> ${APPLICATION_POSITION_NAME}</li>
	<li><b>Start date:</b> ${APPLICATION_START_DATE}</li>
	<li><b>Salary and Conditions:</b> ${APPLICATION_OFFER_CONDITION}</li>
</ul>

${ACTION_COMPLETE}

<p>
	We hope that you will accept and we look forward to welcoming you.
</p>

<#if TEMPLATE_BUFFERED?has_content>  
    <p>${TEMPLATE_BUFFERED}</p>
</#if>
<p>
    Yours sincerely,
    <br/> ${TEMPLATE_SIGNATORY_FULL_NAME}
</p>
