<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>PRiSM</title>
</head>
<body>

<div class="opportunities">
    <div class="prism-connect opportunity list">
        <div class="prism-header">
            <div class="logo">
                <a href="${applicationUrl}" class="navbar-brand" target="_blank"><img
                        src="${applicationUrl}/images/prism_white.png" alt="PRiSM"></a>
            </div>
            <div class="sub-header">${headerTitle}</div>
        </div>

    [#if options.type == "SIMPLE"]
        <div class="prism-main">
            <a href="http://prism.hr/#!/applicant/main?${advert.resource.resourceScope?lower_case}=${advert.resource.id}"
               class="btn btn-success"
               target="_blank">${viewOpportunitiesLabel}</a>
            <a href="http://prism.hr/#!/advertise?context=${advert.resource.resourceScope?lower_case}&&selected${advert.resource.resourceScope?capitalize}=${advert.resource.id}&selectedResourceContext=university"
               class="btn btn-primary" target="_blank">${postOpportunityLabel}</a>
        </div>
    [/#if]
    [#if options.type == "STATIC_LIST"]
        <div class="prism-main static-list">
            <ul>
                [#list opportunities as opportunity]
                    <li>
                        [#include "opportunity_partial.ftl"]
                        <div class="prism-apply-holder">
                            <a href="${applicationUrl}/#!/applicant/main?${opportunity.advert.resource.scope?lower_case}=${opportunity.advert.resource.id}"
                               class="btn btn-success ng-scope" target="_blank">${applyNowLabel}</a>
                        </div>
                    </li>
                [/#list]
            </ul>
        </div>
    [/#if]
    [#if options.type == "SLIDER_LIST"]
    <script src="${applicationUrl}/scripts/slider.js" type="text/javascript"></script>
        <div class="prism-main slider-list">
            <ul>
                [#list opportunities as opportunity]
                <li>
                    [#include "opportunity_partial.ftl"]
                    <div class="prism-apply-holder">
                        <a href="${applicationUrl}/#!/applicant/main?${opportunity.advert.resource.scope?lower_case}=${opportunity.advert.resource.id}"
                           class="btn btn-success ng-scope" target="_blank">${applyNowLabel}</a>
                    </div>
                </li>
                [/#list]
            </ul>
        </div>
    [/#if]
    </div>
</div>

</body>
</html>

