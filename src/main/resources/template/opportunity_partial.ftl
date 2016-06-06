<h1>
    <a href="${applicationUrl}/#!/applicant/main?${opportunity.advert.resource.scope?lower_case}=${opportunity.advert.resource.id}">
    ${opportunity.advert.name}
    </a>
    <span class="label label-primary">${opportunity.opportunityType}</span>
</h1>
<h2>at <span>${opportunity.advert.institution.name}</span></h2>
<div class="short-description-holder">
    <ul class="short-description-details">
        <li>
            <span class="title">${opportunity.availabilityLabel}</span>
            <span>${opportunity.availability} - ${opportunity.studyOptions?join(", ")} ${opportunity.duration!}</span>
        </li>
        <li>
            <span class="title">${opportunity.locationLabel}</span>
        ${opportunity.locations?join(", ")}
        </li>
    [#if opportunity.pay??]
        <li>
            <span class="title">${opportunity.payLabel}</span>
        ${opportunity.pay}
        </li>
    [/#if]
        <li>
        [#if opportunity.advert.closingDate??]
            <span class="title">${opportunity.closingDateLabel}</span>
        ${opportunity.advert.closingDate.toDate()?date}
        [#else]
            <span class="title">${opportunity.noClosingDate}</span>
        [/#if]
        </li>
    </ul>
</div>
<div class="image-container">
    <div class="image-flex">
        <div class="image-logo">
            <img src="${applicationUrl}/prism/api/images/${opportunity.advert.institution.logoImage.id}">
        </div>
    </div>
</div>
<p class="summary">
${opportunity.advert.summary}
</p>

