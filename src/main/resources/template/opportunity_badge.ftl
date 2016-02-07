<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>PRiSM</title>
</head>
<body>

<div class="opportunities">
    <div class="col-sm-8">
        <div class="prism-connect opportunity">
            <div class="prism-header">
                <div class="logo">
                    <a href="${applicationUrl}" class="navbar-brand" target="_blank"><img
                            src="${applicationUrl}/images/prism_white.png" alt="PRiSM"></a>
                </div>
                <div class="sub-header">Jobs and Work experience</div>
            </div>
            <div class="prism-main">
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
                            ${pay},
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
            </div>
            <div class="prism-apply-holder">
                <a href="${applicationUrl}" class="btn btn-default enquire" target="_blank">Other Opportunities</a>
                <a href="${applicationUrl}/#!/applicant/main?${advert.resource.resourceScope?lower_case}=${advert.resource.id}"
                   class="btn btn-success ng-scope" target="_blank">Apply Now</a>
            </div>
        </div>
    </div>
</div>

</body>
</html>

