package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOfferType.CONDITIONAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOfferType.UNCONDITIONAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.utils.PrismConstants.DEFAULT_RATING;
import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentCustomResponse;
import com.zuehlke.pgadmissions.domain.comment.CommentOfferDetail;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ApplicationRatingSummaryDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ApplicationPostprocessor implements ResourceProcessor {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;
    
    @Inject
    private EntityService entityService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

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
            synchronizeApplicationRating(application, comment);
        }

        if (comment.isInterviewScheduledExpeditedComment()) {
            appendInterviewScheduledExpeditedComments(comment);
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

    private void synchronizeApplicationRating(Application application, Comment comment) {
        buildAggregatedRating(comment);
        if (comment.getApplicationRating() == null) {
            comment.setApplicationRating(new BigDecimal(DEFAULT_RATING));
        }

        ApplicationRatingSummaryDTO ratingSummary = applicationService.getApplicationRatingSummary(application);
        application.setApplicationRatingCount(ratingSummary.getApplicationRatingCount().intValue());
        application.setApplicationRatingAverage(BigDecimal.valueOf(ratingSummary.getApplicationRatingAverage()));
        
        entityService.flush();

        for (ResourceParent parent : application.getParentResources()) {
            ApplicationRatingSummaryDTO parentRatingSummary = applicationService.getApplicationRatingSummary(parent);
            Integer ratingCount = parentRatingSummary.getApplicationRatingCount().intValue();
            Integer ratingApplications = parentRatingSummary.getApplicationRatingApplications().intValue();
            parent.setApplicationRatingCount(ratingCount);
            parent.setApplicationRatingFrequency(new BigDecimal(ratingCount).divide(new BigDecimal(ratingApplications), 2, HALF_UP));
            parent.setApplicationRatingAverage(BigDecimal.valueOf(parentRatingSummary.getApplicationRatingAverage()).setScale(2, HALF_UP));
        }
    }

    private void buildAggregatedRating(Comment comment) {
        if (!comment.getCustomResponses().isEmpty()) {
            BigDecimal aggregatedRating = new BigDecimal(0.00);
            for (CommentCustomResponse customResponse : comment.getCustomResponses()) {
                switch (customResponse.getActionCustomQuestionConfiguration().getCustomQuestionType()) {
                case RATING_NORMAL:
                    aggregatedRating = aggregatedRating.add(getWeightedRatingComponent(customResponse, 5));
                    break;
                case RATING_WEIGHTED:
                    aggregatedRating = aggregatedRating.add(getWeightedRatingComponent(customResponse, 8));
                    break;
                default:
                    break;
                }
            }
            comment.setApplicationRating(aggregatedRating);
        }
    }

    private BigDecimal getWeightedRatingComponent(CommentCustomResponse customResponse, Integer denominator) {
        String propertyValue = customResponse.getPropertyValue();
        return new BigDecimal(propertyValue == null ? DEFAULT_RATING.toString() : propertyValue).divide(new BigDecimal(denominator))
                .multiply(new BigDecimal(5)).multiply(customResponse.getActionCustomQuestionConfiguration().getWeighting()).setScale(2, RoundingMode.HALF_UP);
    }

    private void appendInterviewScheduledExpeditedComments(Comment comment) {
        LocalDateTime interviewDateTime = comment.getInterviewAppointment().getInterviewDateTime();
        comment.getAppointmentTimeslots().add(new CommentAppointmentTimeslot().withDateTime(interviewDateTime));

        Resource resource = comment.getResource();
        PrismAction prismAction = APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
        Action action = actionService.getById(prismAction);
        DateTime baseline = comment.getCreatedTimestamp();

        User invoker = comment.getUser();
        List<User> users = commentService.getAssignedUsers(comment, APPLICATION_INTERVIEWER, APPLICATION_INTERVIEWEE);
        for (User user : users) {
            Comment preferenceComment = commentService.createInterviewPreferenceComment(resource, action, invoker, user, interviewDateTime, baseline);
            commentService.persistComment(resource, preferenceComment);
            resource.addComment(preferenceComment);
        }
    }

    private void synchronizeOfferRecommendation(Application application, Comment comment) {
        CommentOfferDetail offerDetail = comment.getOfferDetail();
        if (offerDetail != null) {
            application.setConfirmedStartDate(offerDetail.getPositionProvisionalStartDate());
            application.setConfirmedOfferType(offerDetail.getAppointmentConditions() == null ? UNCONDITIONAL : CONDITIONAL);
        }
        application.getUser().getUserAccount().setSendApplicationRecommendationNotification(false);

        if (!comment.getAssignedUsers().isEmpty()) {
            List<User> connections = Lists.newLinkedList(roleService.getRoleUsers(application, APPLICATION_PRIMARY_SUPERVISOR));
            connections.addAll(roleService.getRoleUsers(application, APPLICATION_SECONDARY_SUPERVISOR));
            connections.addAll(roleService.getRoleUsers(application, APPLICATION_CREATOR));
            userService.createUserConnections(connections);
        }
    }

}
