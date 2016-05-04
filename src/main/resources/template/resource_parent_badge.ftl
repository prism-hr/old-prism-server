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
            <div class="sub-header">Jobs and Work experience</div>
        </div>

    [#if options.type == "SIMPLE"]
        <div class="prism-main">
            <a href="http://prism.hr/#!/applicant/main?${advert.resource.resourceScope?lower_case}=${advert.resource.id}"
               class="btn btn-success"
               target="_blank">View Opportunities</a>
            <a href="http://prism.hr/#!/advertise?context=${advert.resource.resourceScope?lower_case}&&selected${advert.resource.resourceScope?capitalize}=${advert.resource.id}&selectedResourceContext=university"
               class="btn btn-primary" target="_blank">Post Opportunities</a>
        </div>
    [/#if]
    [#if options.type == "STATIC_LIST"]
        <div class="prism-main static-list">
            <ul>
                <li>
                    <#include "/template/opportunity_partial.ftl">
                </li>
            </ul>
        </div>
    [/#if]
    [#if options.type == "SLIDER_LIST"]
        <div class="prism-main slider-list">
            <ul>
                <li>
                    <#include "/template/opportunity_partial.ftl">
                </li>
            </ul>
        </div>
    [/#if]
    </div>
</div>

</body>
</html>

