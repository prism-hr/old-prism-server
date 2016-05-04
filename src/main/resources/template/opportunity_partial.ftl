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