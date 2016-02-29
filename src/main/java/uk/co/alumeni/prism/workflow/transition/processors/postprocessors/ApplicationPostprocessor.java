package uk.co.alumeni.prism.workflow.transition.processors.postprocessors;

import static java.math.RoundingMode.HALF_UP;
import static uk.co.alumeni.prism.PrismConstants.CONFIDENCE_MEDIUM;
import static uk.co.alumeni.prism.PrismConstants.DEFAULT_RATING;
import static uk.co.alumeni.prism.PrismConstants.RATING_PRECISION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.application.ApplicationReferee;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentAppointmentTimeslot;
import uk.co.alumeni.prism.domain.comment.CommentCompetence;
import uk.co.alumeni.prism.domain.comment.CommentOfferDetail;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ResourceRatingSummaryDTO;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.EntityService;
import uk.co.alumeni.prism.services.ScopeService;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.workflow.transition.processors.ResourceProcessor;

import com.google.common.collect.Lists;

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
    private ScopeService scopeService;

    @Inject
    private UserService userService;

    @Override
    public void process(Application resource, Comment comment) {
        if (comment.isApplicationProvideReferenceComment()) {
            synchronizeApplicationReferees(resource, comment);
        }

        if (comment.isRatingComment(APPLICATION)) {
            synchronizeApplicationRating(resource, comment);
        }

        if (comment.isInterviewScheduledExpeditedComment()) {
            appendInterviewScheduledExpeditedComments(comment);
        }

        if (comment.isApplicationConfirmOfferRecommendationComment()) {
            synchronizeOfferRecommendation(resource, comment);
        }

        if (comment.isApplicationProcessingCompletedComment()) {
            resource.setCompletionDate(comment.getSubmittedTimestamp().toLocalDate());
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

        applicationService.synchronizeApplicationRating(application);
        entityService.flush();

        for (PrismScope scope : scopeService.getParentScopesDescending(APPLICATION, INSTITUTION)) {
            ResourceParent parent = (ResourceParent) application.getEnclosingResource(scope);
            if (parent != null) {
                ResourceRatingSummaryDTO parentRatingSummary = applicationService.getApplicationRatingSummary(parent);
                Integer ratingCount = parentRatingSummary.getRatingCount().intValue();
                Integer ratingApplications = parentRatingSummary.getResourceCount().intValue();
                parent.setApplicationRatingCount(ratingCount);
                parent.setApplicationRatingFrequency(new BigDecimal(ratingCount).divide(new BigDecimal(ratingApplications), RATING_PRECISION, HALF_UP));
                parent.setApplicationRatingAverage(BigDecimal.valueOf(parentRatingSummary.getRatingAverage()).setScale(RATING_PRECISION, HALF_UP));
            }
        }
    }

    private void buildAggregatedRating(Comment comment) {
        Set<CommentCompetence> competences = comment.getCompetences();
        if (!competences.isEmpty()) {
            Map<Integer, Integer> importances = advertService.getCompetenceImportances(comment.getResource().getAdvert());

            List<Integer> scores = Lists.newArrayList();
            BigDecimal sumImportance = new BigDecimal(0).setScale(RATING_PRECISION);
            for (CommentCompetence competence : competences) {
                Integer importance = importances.get(competence.getId());
                importance = importance == null ? CONFIDENCE_MEDIUM : importance;
                scores.add(importance * competence.getRating());
                sumImportance = sumImportance.add(new BigDecimal(importance).setScale(RATING_PRECISION));
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
        }
    }

}
