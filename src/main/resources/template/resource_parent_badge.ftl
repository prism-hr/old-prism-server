<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>PRiSM</title>
</head>
<body>

<div class="opportunities">
[#if options.type == "SIMPLE"]
<div class="prism-connect">
[#else]
<div class="prism-connect opportunity list">
[/#if]
    <div class="prism-header">
        <div class="logo">
            <a href="${applicationUrl}" class="navbar-brand" target="_blank"><img
                    src="${applicationUrl}/images/prism_white.png" alt="PRiSM"></a>
        </div>
        <div class="sub-header">${headerTitle}</div>
    [#if options.type == "SLIDER_LIST"]
        <div class="control">
            <a class="btn control_prev"> &#60 </a>
            <span class="position-number"></span> /
            <span class="position-total"></span>
            <a class="btn control_next"> &#62 </a>
        </div>
    [/#if]
    </div>

[#if options.type == "SIMPLE"]
    <div class="prism-main">
        <a href="${applicationUrl}/#!/applicant/main?${advert.resource.resourceScope?lower_case}=${advert.resource.id}"
           class="btn btn-success"
           target="_blank">${viewOpportunitiesLabel}</a>
        <a href="${applicationUrl}/#!/advertise?context=${advert.resource.resourceScope?lower_case}&&selected${advert.resource.resourceScope?capitalize}=${advert.resource.id}&selectedResourceContext=${options.context}"
           class="btn btn-primary" target="_blank">${postOpportunityLabel}</a>
    </div>

[#else] [#-- LIST --]

    <div class="prism-main ${options.type?replace("_", "-")?lower_case}">
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
            [#if invisibleAdvertCount > 0]
                <li class="prism-summary clearfix">
                    <div class="projects">
                        <div class="content">
                            <div class="title">
                                <span class="ng-binding">${invisibleAdvertCount} more</span>
                                Private <br>Opportunities
                            </div>
                        </div>
                    </div>
                    <div class="companies-list">
                        <h2>Opportunities from: </h2>
                        <ul>
                            [#list invisibleAdvertInstitutions as institution]
                                <li>${institution.name}: <strong>${institution.occurrenceCount}</strong></li>
                            [/#list]
                        </ul>

                        <p>To apply for the private applications you need to

                            [#if advert.resource.resourceScope == "DEPARTMENT"]
                            <a href="${applicationUrl}/#!/university/applicant?department=${advert.resource.id}"
                               class="btn btn-success inverted btn-xs">
                            [#else]
                            <a href="${applicationUrl}/#!/university/applicant"
                               class="btn btn-success inverted btn-xs">
                            [/#if]
                            Register as a student
                        </a>
                        </p>
                    </div>
                </li>
            [/#if]
        </ul>
    </div>

    <div class="prism-footer">
        <a href="${applicationUrl}/#!/applicant/main?${advert.resource.resourceScope?lower_case}=${advert.resource.id}"
           class="btn btn-success"
           target="_blank">${viewOpportunitiesLabel}</a>
        <a href="${applicationUrl}/#!/advertise?context=${advert.resource.resourceScope?lower_case}&&selected${advert.resource.resourceScope?capitalize}=${advert.resource.id}&selectedResourceContext=${options.context}"
           class="btn btn-primary" target="_blank">${postOpportunityLabel}</a>
    </div>
[/#if]
</div>
</div>

</body>
</html>
