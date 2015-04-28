package com.zuehlke.pgadmissions.workflow.resourcer.processors.postprocessors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOfferType.CONDITIONAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOfferType.UNCONDITIONAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;
import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationOfferDetail;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationRatingSummaryDTO;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.workflow.resourcer.processors.ResourceProcessor;

@Component
public class ApplicationPostprocessor implements ResourceProcessor {
    
    @Inject
    private ApplicationService applicationService;
    
    @Inject
    private RoleService roleService;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Application application = (Application) resource;
        
        if (comment.isProjectCreateApplicationComment()) {
            synchronizeProjectSupervisors(application);
        }

        if (comment.isApplicationProvideReferenceComment()) {
            synchronizeApplicationReferees(application, comment);
        }

        if (comment.isApplicationRatingComment()) {
            syncrhonizeApplicationRating(application);
        }

        if (comment.isApplicationConfirmOfferRecommendationComment()) {
            synchronizeOfferRecommendation(application, comment);
        }

        if (comment.isApplicationReserveStatusComment()) {
            application.setApplicationReserveStatus(comment.getApplicationReserveStatus());
        }

        if (comment.isApplicationCompletionComment()) {
            application.setCompletionDate(comment.getCreatedTimestamp().toLocalDate());
        }
    }
    
    private void synchronizeProjectSupervisors(Application application) {
        List<User> supervisorUsers = roleService.getRoleUsers(application.getProject(), PROJECT_SUPERVISOR_GROUP);
        for (User supervisorUser : supervisorUsers) {
            application.getSupervisors().add(
                    new ApplicationSupervisor().withUser(supervisorUser).withAcceptedSupervision(true).withLastUpdatedTimestamp(new DateTime()));
        }
    }

    private void synchronizeApplicationReferees(Application application, Comment comment) {
        ApplicationReferee referee = applicationService.getApplicationReferee(application, comment.getActionOwner());
        referee.setComment(comment);
    }

    private void synchronizeOfferRecommendation(Application application, Comment comment) {
        CommentApplicationOfferDetail offerDetail = comment.getOfferDetail();
        if (offerDetail != null) {
            application.setConfirmedStartDate(offerDetail.getPositionProvisionalStartDate());
            application.setConfirmedOfferType(offerDetail.getAppointmentConditions() == null ? UNCONDITIONAL : CONDITIONAL);
        }
        application.getUser().getUserAccount().setSendApplicationRecommendationNotification(false);
    }

    private void syncrhonizeApplicationRating(Application application) {
        for (ResourceParent parent : application.getParentResources()) {
            ApplicationRatingSummaryDTO ratingSummary = applicationService.getApplicationRatingSummary(parent);
            Integer ratingCount = ratingSummary.getApplicationRatingCount().intValue();
            Integer ratingApplications = ratingSummary.getApplicationRatingApplications().intValue();
            parent.setApplicationRatingCount(ratingCount);
            parent.setApplicationRatingFrequency(new BigDecimal(ratingCount).divide(new BigDecimal(ratingApplications).setScale(2, HALF_UP)));
            parent.setApplicationRatingAverage(BigDecimal.valueOf(ratingSummary.getApplicationRatingAverage()).setScale(2, HALF_UP));
        }
    }

}
