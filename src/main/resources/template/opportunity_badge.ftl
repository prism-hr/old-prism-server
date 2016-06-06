<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>PRiSM</title>
</head>
<body>

<div class="opportunities">
    <div class="prism-connect opportunity">
        <div class="prism-header">
            <div class="logo">
                <a href="${applicationUrl}" class="navbar-brand" target="_blank"><img
                        src="${applicationUrl}/images/prism_white.png" alt="PRiSM"></a>
            </div>
            <div class="sub-header">${headerTitle}</div>
        </div>
        <div class="prism-main">
        [#if opportunity??]
            [#include "opportunity_partial.ftl"]
        [#else]
            <h1>Opportunity Not Available</h1>
        [/#if]
        </div>
        <div class="prism-apply-holder">
            <a href="${applicationUrl}/#!/${options.context?lower_case}/applicant?institution=${advert.institution.id}&tab=opportunities"
               class="btn btn-default enquire" target="_blank">${otherOpportunitiesLabel}</a>
        [#if opportunity??]
            <a href="${applicationUrl}/#!/applicant/main?${advert.resource.resourceScope?lower_case}=${advert.resource.id}"
               class="btn btn-success ng-scope" target="_blank">${readMoreLabel}</a>
        [/#if]
        </div>
    </div>
</div>

</body>
</html>

