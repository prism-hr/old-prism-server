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
                    <h1>
                        <a href="${applicationUrl}/#!/applicant/main?${advert.resource.resourceScope?lower_case}=${advert.resource.id}">
                        ${advert.name}
                        </a>
                        <span class="label label-primary">${opportunityType}</span>
                    </h1>

                    <p class="summary">
                    ${advert.summary}
                    </p>

                    <div class="short-description-holder">
                        <ul class="short-description-details">
                            <li>
                                <span class="title">${availabilityLabel}</span>
                                <span>${availability} - ${studyOptions?join(", ")} ${duration?if_exists}</span>
                            </li>
                            <li>
                                <span class="title">${locationLabel}</span>
                            ${locations?join(", ")}
                            </li>
                            [#if pay??]
                                <li>
                                    <span class="title">${payLabel}</span>
                                ${pay}
                                </li>
                            [/#if]
                            <li>
                                [#if advert.closingDate??]
                                    <span class="title">${closingDateLabel}</span>
                                ${advert.closingDate.toDate()?date}
                                [#else]
                                    <span class="title">${noClosingDate}</span>
                                [/#if]
                            </li>
                        </ul>
                    </div>
                    <div class="prism-apply-holder">
                        <a href="${applicationUrl}/#!/employer/applicant?institution=${advert.institution.id}&tab=opportunities" class="btn btn-default enquire" target="_blank">Other Opportunities</a>
                        <a href="${applicationUrl}/#!/applicant/main?${advert.resource.resourceScope?lower_case}=${advert.resource.id}"
                           class="btn btn-success ng-scope" target="_blank">Apply Now</a>
                    </div>
                </li>
            </ul>
        </div>
    [/#if]
    [#if options.type == "SLIDER_LIST"]
        <div class="prism-main">
            Here goes slider list
        </div>
    [/#if]
    </div>
</div>

</body>
</html>

