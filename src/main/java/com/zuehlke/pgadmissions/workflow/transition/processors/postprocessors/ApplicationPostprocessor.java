package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import static com.zuehlke.pgadmissions.PrismConstants.DEFAULT_RATING;
import static com.zuehlke.pgadmissions.PrismConstants.RATING_PRECISION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOfferType.CONDITIONAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOfferType.UNCONDITIONAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_IDENTIFICATION_STATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.ListUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentCompetence;
import com.zuehlke.pgadmissions.domain.comment.CommentOfferDetail;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.resource.ResourceRatingSummaryDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ApplicationPostprocessor implements ResourceProcessor<Application> {

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private CommentService commentService;

    @Inject
    private EntityService entityService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private RoleService roleService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private UserService userService;

    @Override
    public void process(Application resource, Comment comment) {
        if (comment.isProjectCreateApplicationComment()) {
            synchronizeProjectSupervisors(resource);
        }

        if (comment.isApplicationIdentifiedComment()) {
            synchronizeUserAdverts(resource, comment);
        }

        if (comment.isApplicationProvideReferenceComment()) {
            synchronizeApplicationReferees(resource, comment);
        }

        if (comment.isApplicationRatingComment()) {
            synchronizeApplicationRating(resource, comment);
        }

        if (comment.isInterviewScheduledExpeditedComment()) {
            appendInterviewScheduledExpeditedComments(comment);
        }

        if (comment.isApplicationConfirmOfferRecommendationComment()) {
            synchronizeOfferRecommendation(resource, comment);
        }

        if (comment.isApplicationReserveStatusComment()) {
            resource.setApplicationReserveStatus(comment.getApplicationReserveStatus());
        }

        if (comment.isApplicationCompletionComment()) {
            resource.setCompletionDate(comment.getCreatedTimestamp().toLocalDate());
        }
    }

    private void synchronizeProjectSupervisors(Application application) {
        List<User> supervisorUsers = roleService.getRoleUsers(application.getProject(), PROJECT_SUPERVISOR_GROUP);
        for (User supervisorUser : supervisorUsers) {
            application.getSupervisors().add(
                    new ApplicationSupervisor().withUser(supervisorUser).withAcceptedSupervision(true).withLastUpdatedTimestamp(new DateTime()));
        }
    }

    @SuppressWarnings("unchecked")
    private void synchronizeUserAdverts(Application application, Comment comment) {
        User applicant = application.getUser();

        List<Integer> targetAdverts = advertService.getAdvertSelectedTargetAdverts(application.getAdvert());
        List<Integer> targetAdvertsMatched = advertService.getAdvertsToIdentifyUserFor(applicant, targetAdverts);

        if (!(targetAdverts.isEmpty() || targetAdvertsMatched.isEmpty())) {
            List<Integer> targetAdvertsToProvideIdentificationFor = Lists.newArrayList();
            for (UserRole userRole : roleService.getActionPerformerUserRoles(comment.getUser(), APPLICATION_COMPLETE_IDENTIFICATION_STATE)) {
                Resource userResource = userRole.getResource();
                if (userResource.getResourceScope().equals(SYSTEM)) {
                    targetAdvertsToProvideIdentificationFor = ListUtils.intersection(targetAdverts, targetAdvertsMatched);
                    break;
                }

                Integer advertId = userRole.getResource().getAdvert().getId();
                if (targetAdverts.contains(advertId) && targetAdvertsMatched.contains(advertId)) {
                    targetAdvertsToProvideIdentificationFor.add(advertId);
                }
            }

            if (!targetAdvertsToProvideIdentificationFor.isEmpty()) {
                advertService.identifyForAdverts(applicant, targetAdvertsToProvideIdentificationFor);
            }
        }
    }

    private void synchronizeApplicationReferees(Application application, Comment comment) {
        ApplicationReferee referee = applicationService.getApplicationReferee(application, comment.getActionOwner());
        referee.setComment(comment);
    }

    private void synchronizeApplicationRating(Application application, Comment comment) {
        buildAggregatedRating(comment);
        if (comment.getRating() == null) {
            comment.setRating(new BigDecimal(DEFAULT_RATING));
        }

        applicationService.syncronizeApplicationRating(application);
        entityService.flush();

        scopeService.getParentScopesDescending(APPLICATION, INSTITUTION).forEach(scope -> {
            ResourceParent parent = (ResourceParent) application.getEnclosingResource(scope);
            ResourceRatingSummaryDTO parentRatingSummary = applicationService.getApplicationRatingSummary(parent);
            Integer ratingCount = parentRatingSummary.getRatingCount().intValue();
            Integer ratingApplications = parentRatingSummary.getRatingResources().intValue();
            parent.setApplicationRatingCount(ratingCount);
            parent.setApplicationRatingFrequency(new BigDecimal(ratingCount).divide(new BigDecimal(ratingApplications), RATING_PRECISION, HALF_UP));
            parent.setApplicationRatingAverage(BigDecimal.valueOf(parentRatingSummary.getRatingAverage()).setScale(RATING_PRECISION, HALF_UP));
        });
    }

    private void buildAggregatedRating(Comment comment) {
        Set<CommentCompetence> competences = comment.getCompetences();
        if (!competences.isEmpty()) {
            List<Integer> scores = Lists.newArrayList();
            BigDecimal sumImportance = new BigDecimal(0).setScale(0);
            for (CommentCompetence competence : competences) {
                Integer importance = competence.getImportance();
                scores.add(importance * competence.getRating());
                sumImportance = sumImportance.add(new BigDecimal(importance).setScale(0));
            }

            BigDecimal rating = new BigDecimal(0).setScale(RATING_PRECISION, HALF_UP);
            for (Integer score : scores) {
                rating = rating.add(new BigDecimal(score).divide(sumImportance, RATING_PRECISION, HALF_UP));
            }

            comment.setRating(rating);
            userService.updateUserCompetence(comment.getApplication().getUser());
        }
    }

    private void appendInterviewScheduledExpeditedComments(Comment comment) {
        LocalDateTime interviewDateTime = comment.getInterviewAppointment().getInterviewDateTime();
        comment.getAppointmentTimeslots().add(new CommentAppointmentTimeslot().withDateTime(interviewDateTime));

        PrismAction prismAction = APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
        Action action = actionService.getById(prismAction);
        DateTime baseline = comment.getCreatedTimestamp();

        User invoker = comment.getUser();
        Application application = comment.getApplication();
        List<User> users = commentService.getAssignedUsers(comment, APPLICATION_INTERVIEWER, APPLICATION_INTERVIEWEE);
        for (User user : users) {
            Comment preferenceComment = commentService.createInterviewPreferenceComment(application, action, invoker, user, interviewDateTime, baseline);
            commentService.persistComment(application, preferenceComment);
            application.addComment(preferenceComment);
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
