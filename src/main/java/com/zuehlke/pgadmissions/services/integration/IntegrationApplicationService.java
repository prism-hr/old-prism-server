package com.zuehlke.pgadmissions.services.integration;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.doubleToBigDecimal;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.longToInteger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationStudyDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationExportRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationInterviewRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationPersonalDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationProgramDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStudyDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSupervisorRepresentation;
import com.zuehlke.pgadmissions.services.CommentService;

@Service
@Transactional
public class IntegrationApplicationService {

    @Inject
    private CommentService commentService;
    
    @Inject
    private IntegrationCommentService integrationCommentService;

    @Inject
    private IntegrationImportedEntityService integrationImportedEntityService;

    @Inject
    private IntegrationResourceService integrationResourceService;

    @Inject
    private IntegrationUserService integrationUserService;

    public ApplicationClientRepresentation getApplicationClientRepresentation(Application application) throws Exception {
        ApplicationClientRepresentation representation = (ApplicationClientRepresentation) getApplicationRepresentation(application, null);
        
        representation.setInterview(getApplicationInterviewRepresentation(application));

        return representation;
    }

    public ApplicationExportRepresentation getApplicationExportRepresentation(Application application) throws Exception {
        return (ApplicationExportRepresentation) getApplicationRepresentation(application, application.getInstitution());
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

    private ApplicationRepresentation getApplicationRepresentation(Application application, Institution institution) throws Exception {
        ApplicationRepresentation representation = (ApplicationRepresentation) integrationResourceService.getResourceRepresentationExtended(application);
        representation.setClosingDate(application.getClosingDate());
        representation.setSubmittedTimestamp(application.getSubmittedTimestamp());
        representation.setPreviousApplication(application.getPreviousApplication());
        representation.setProgramDetail(getApplicationProgramDetailRepresentation(application, institution));
        representation.setStudyDetail(getApplicationStudyDetailRepresentation(application));
        representation.setPrimaryThemes(getApplicationThemeRepresentation(application.getPrimaryTheme()));
        representation.setSecondaryThemes(getApplicationThemeRepresentation(application.getSecondaryTheme()));
        representation.setSupervisors(getApplicationSupervisorsRepresentation(application));
        representation.setPersonalDetail(getApplicationPersonalDetailRepresentation(application, institution));
        return representation;
    }

    private ApplicationProgramDetailRepresentation getApplicationProgramDetailRepresentation(Application application, Institution institution) {
        ApplicationProgramDetail applicationProgramDetail = application.getProgramDetail();
        return new ApplicationProgramDetailRepresentation()
                .withStudyOption(getImportedEntityRepresentation(applicationProgramDetail.getStudyOption(), institution))
                .withStartDate(applicationProgramDetail.getStartDate())
                .withReferralSource(getImportedEntityRepresentation(applicationProgramDetail.getReferralSource(), institution));
    }

    private ApplicationStudyDetailRepresentation getApplicationStudyDetailRepresentation(Application application) {
        ApplicationStudyDetail applicationStudyDetail = application.getStudyDetail();
        return new ApplicationStudyDetailRepresentation().withStudyLocation(applicationStudyDetail.getStudyLocation()).withStudyDivision(
                applicationStudyDetail.getStudyDivision()).withStudyArea(applicationStudyDetail.getStudyArea())
                .withStudyApplicationId(applicationStudyDetail.getStudyApplicationId()).withStudyStartDate(applicationStudyDetail.getStudyStartDate());
    }

    private List<ApplicationSupervisorRepresentation> getApplicationSupervisorsRepresentation(Application application) {
        List<ApplicationSupervisorRepresentation> representations = Lists.newLinkedList();
        for (ApplicationSupervisor applicationSupervisor : application.getSupervisors()) {
            representations.add(getApplicationSupervisorRepresentation(applicationSupervisor));
        }
        return representations;
    }

    private ApplicationSupervisorRepresentation getApplicationSupervisorRepresentation(ApplicationSupervisor applicationSupervisor) {
        return new ApplicationSupervisorRepresentation().withId(applicationSupervisor.getId())
                .withUser(integrationUserService.getUserRepresentationSimple(applicationSupervisor.getUser()))
                .withAcceptedSupervisor(applicationSupervisor.getAcceptedSupervision());
    }

    private ApplicationPersonalDetailRepresentation getApplicationPersonalDetailRepresentation(Application application, Institution institution) {
        ApplicationPersonalDetail applicationPersonalDetail = application.getPersonalDetail();
        return new ApplicationPersonalDetailRepresentation().withTitle(getImportedEntityRepresentation(applicationPersonalDetail.getTitle(), institution))
                .withGender(getImportedEntityRepresentation(applicationPersonalDetail.getGender(), institution))
                .withDateOfBirth(applicationPersonalDetail.getDateOfBirth())
                .withAgeRange(getImportedEntityRepresentation(applicationPersonalDetail.getAgeRange(), institution));
    }

    private <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> ImportedEntitySimpleRepresentation getImportedEntityRepresentation(T entity,
            Institution institution) {
        return entity == null ? null : integrationImportedEntityService.getImportedEntityRepresentation(entity, institution);
    }

    private List<String> getApplicationThemeRepresentation(String themes) {
        return StringUtils.isEmpty(themes) ? Collections.<String> emptyList() : Arrays.asList(themes.split("|"));
    }

    private ApplicationInterviewRepresentation getApplicationInterviewRepresentation(Application application) {
        Comment schedulingComment = commentService.getLatestComment(application, APPLICATION_ASSIGN_INTERVIEWERS);
        
        if (schedulingComment != null) {
            ApplicationInterviewRepresentation representation = new ApplicationInterviewRepresentation();
            
            Set<CommentAppointmentTimeslot> timeslots = schedulingComment.getAppointmentTimeslots();
            representation.setAppointmentTimeslots(integrationCommentService.getCommentAppointmentTimeslotRepresentations(timeslots));
            representation.setAppointmentPreferences(integrationCommentService.getCommentAppointmentPreferenceRepresentations(schedulingComment, timeslots));
            
            representation.setInterviewAppointment(integrationCommentService.getCommentInterviewAppointmentRepresentation(schedulingComment));
            representation.setInterviewInstruction(integrationCommentService.getCommentInterviewInstructionRepresentation(schedulingComment, true));
    
            return representation;
        }
        
        return null;
    }

}
