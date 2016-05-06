<!DOCTYPE html>
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
                [#include "opportunity_partial.ftl"]
            </div>
            <div class="prism-apply-holder">
                <a href="${applicationUrl}/#!/employer/applicant?institution=${opportunity.advert.institution.id}&tab=opportunities" class="btn btn-default enquire" target="_blank">Other Opportunities</a>
                <a href="${applicationUrl}/#!/applicant/main?${opportunity.advert.resource.resourceScope?lower_case}=${opportunity.advert.resource.id}"
                   class="btn btn-success ng-scope" target="_blank">Apply Now</a>
            </div>
        </div>
    </div>
</div>

</body>
</html>

