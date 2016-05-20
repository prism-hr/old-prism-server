<a href="${applicationUrl}/#!/${options.context?lower_case}/applicant?${advert.resource.resourceScope?lower_case}=${advert.resource.id}&tab=opportunities"
   class="btn btn-success"
   target="_blank">${viewOpportunitiesLabel}</a>
[#if options.context == "UNIVERSITY"]
<a href="${applicationUrl}/#!/university/employer?${advert.resource.resourceScope?lower_case}=${advert.resource.id}"
   class="btn btn-primary" target="_blank">${postOpportunityLabel}</a>
[#else]
<a href="${applicationUrl}/#!/advertise/project?${advert.resource.resourceScope?lower_case}=${advert.resource.id}"
   class="btn btn-primary" target="_blank">${postOpportunityLabel}</a>
[/#if]
