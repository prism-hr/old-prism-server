package com.zuehlke.pgadmissions.services.integration;

import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.doubleToBigDecimal;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.longToInteger;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationStudyDetail;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationExportRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationProgramDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStudyDetailRepresentation;

@Service
@Transactional
public class IntegrationApplicationService {

    @Inject
    private IntegrationImportedEntityService integrationImportedEntityService;

    @Inject
    private IntegrationResourceService integrationResourceService;

    public ApplicationClientRepresentation getApplicationClientRepresentation(Application application) throws Exception {
        ApplicationClientRepresentation representation = (ApplicationClientRepresentation) getApplicationRepresentation(application, false);

        return representation;
    }

    public ApplicationExportRepresentation getApplicationExportRepresentation(Application application) throws Exception {
        return (ApplicationExportRepresentation) getApplicationRepresentation(application, true);
    }

    public ApplicationProcessingSummaryRepresentation getApplicationProcessingSummaryRepresentation(ApplicationProcessingSummaryDTO applicationProcessingSummary) {
        ApplicationProcessingSummaryRepresentation representation = new ApplicationProcessingSummaryRepresentation();
        representation.setAdvertCount(longToInteger(applicationProcessingSummary.getAdvertCount()));
        representation.setSubmittedApplicationCount(longToInteger(applicationProcessingSummary.getSubmittedApplicationCount()));
        representation.setApprovedApplicationCount(longToInteger(applicationProcessingSummary.getApprovedApplicationCount()));
        representation.setRejectedApplicationCount(longToInteger(applicationProcessingSummary.getRejectedApplicationCount()));
        representation.setWithdrawnApplicationCount(longToInteger(applicationProcessingSummary.getWithdrawnApplicationCount()));
        representation.setSubmittedApplicationRatio(doubleToBigDecimal(applicationProcessingSummary.getSubmittedApplicationRatio(), 2));
        representation.setApprovedApplicationRatio(doubleToBigDecimal(applicationProcessingSummary.getApprovedApplicationRatio(), 2));
        representation.setRejectedApplicationRatio(doubleToBigDecimal(applicationProcessingSummary.getRejectedApplicationRatio(), 2));
        representation.setWithdrawnApplicationRatio(doubleToBigDecimal(applicationProcessingSummary.getWithdrawnApplicationRatio(), 2));
        representation.setAverageRating(doubleToBigDecimal(applicationProcessingSummary.getAverageRating(), 2));
        representation.setAverageProcessingTime(doubleToBigDecimal(applicationProcessingSummary.getAverageProcessingTime(), 2));
        return representation;
    }

    private ApplicationRepresentation getApplicationRepresentation(Application application, boolean map) throws Exception {
        ApplicationRepresentation representation = (ApplicationRepresentation) integrationResourceService.getResourceRepresentationExtended(application);

        representation.setClosingDate(application.getClosingDate());
        representation.setSubmittedTimestamp(application.getSubmittedTimestamp());
        representation.setPreviousApplication(application.getPreviousApplication());

        representation.setProgramDetail(getApplicationProgramDetailRepresentation(application, map));
        representation.setStudyDetail(getApplicationStudyDetailRepresentation(application));

        return representation;
    }

    private ApplicationProgramDetailRepresentation getApplicationProgramDetailRepresentation(Application application, boolean map) {
        Institution institution = application.getInstitution();
        ApplicationProgramDetail applicationProgramDetail = application.getProgramDetail();

        return new ApplicationProgramDetailRepresentation()
                .withStudyOption(integrationImportedEntityService.getImportedEntityRepresentation(institution,
                        (ImportedEntitySimple) applicationProgramDetail.getStudyOption(), map))
                .withStartDate(applicationProgramDetail.getStartDate())
                .withReferralSource(
                        integrationImportedEntityService.getImportedEntityRepresentation(institution,
                                (ImportedEntitySimple) applicationProgramDetail.getReferralSource(), map));
    }

    private ApplicationStudyDetailRepresentation getApplicationStudyDetailRepresentation(Application application) {
        ApplicationStudyDetail applicationStudyDetail = application.getStudyDetail();
        return new ApplicationStudyDetailRepresentation().withStudyLocation(applicationStudyDetail.getStudyLocation()).withStudyDivision(
                applicationStudyDetail.getStudyDivision()).withStudyArea(applicationStudyDetail.getStudyArea())
                .withStudyApplicationId(applicationStudyDetail.getStudyApplicationId()).withStudyStartDate(applicationStudyDetail.getStudyStartDate());
    }

}
