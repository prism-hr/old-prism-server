package uk.co.alumeni.prism.workflow.executors.action;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_PARTNER_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.application.ApplicationReferee;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentAssignedUser;
import uk.co.alumeni.prism.domain.comment.CommentOfferDetail;
import uk.co.alumeni.prism.domain.comment.CommentPositionDetail;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.exceptions.PrismValidationException;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentOfferDetailDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentPositionDetailDTO;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.EntityService;
import uk.co.alumeni.prism.services.UserService;

@Component
public class ApplicationExecutor implements ActionExecutor {

    @Inject
    private ActionService actionService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private CommentService commentService;

    @Inject
    private EntityService entityService;

    @Inject
    private UserService userService;

    @Override
    public ActionOutcomeDTO execute(CommentDTO commentDTO) {
        Application application = entityService.getById(Application.class, commentDTO.getResource().getId());
        PrismAction actionId = commentDTO.getAction();

        User user = userService.getById(commentDTO.getUser());
        boolean isCompleteAction = actionId.equals(APPLICATION_COMPLETE);
        if (isCompleteAction) {
            BeanPropertyBindingResult errors = applicationService.validateApplication(application);
            if (errors.hasErrors()) {
                throw new PrismValidationException("Application not completed", errors);
            }

            application.setShared(commentDTO.getShared());
            application.setOnCourse(commentDTO.getOnCourse());
        }

        Action action = actionService.getById(actionId);
        User delegateUser = userService.getById(commentDTO.getDelegateUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());

        Comment comment = new Comment().withUser(user).withResource(application).withContent(commentDTO.getContent()).withDelegateUser(delegateUser)
                .withAction(action).withTransitionState(transitionState).withRating(commentDTO.getRating()).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(BooleanUtils.isTrue(commentDTO.getDeclinedResponse())).withEligible(commentDTO.getEligible()).withInterested(commentDTO.getInterested())
                .withInterviewAvailable(commentDTO.getInterviewAvailable()).withRecruiterAcceptAppointment(commentDTO.getRecruiterAcceptAppointment())
                .withPartnerAcceptAppointment(commentDTO.getPartnerAcceptAppointment()).withApplicantAcceptAppointment(commentDTO.getApplicantAcceptAppointment())
                .withRejectionReason(commentDTO.getRejectionReason());

        if (isCompleteAction) {
            comment.setShared(commentDTO.getShared());
            comment.setOnCourse(commentDTO.getOnCourse());
        }

        CommentPositionDetailDTO positionDetailDTO = commentDTO.getPositionDetail();
        if (positionDetailDTO != null) {
            comment.setPositionDetail(new CommentPositionDetail().withPositionName(positionDetailDTO.getPositionName()).withPositionDescription(
                    positionDetailDTO.getPositionDescription()));
        }

        CommentOfferDetailDTO offerDetailDTO = commentDTO.getOfferDetail();
        if (offerDetailDTO != null) {
            comment.setOfferDetail(new CommentOfferDetail().withPositionProvisionStartDate(offerDetailDTO.getPositionProvisionalStartDate())
                    .withAppointmentConditions(offerDetailDTO.getAppointmentConditions()));
        }

        commentService.appendCommentProperties(comment, commentDTO);
        commentService.appendCommentApplicationProperties(comment, commentDTO);

        if (isCompleteAction) {
            Role refereeRole = entityService.getById(Role.class, APPLICATION_REFEREE);
            for (ApplicationReferee referee : application.getReferees()) {
                comment.getAssignedUsers().add(new CommentAssignedUser().withUser(referee.getUser()).withRole(refereeRole));
            }
        }

        if (commentDTO.getAppointmentTimeslots() != null) {
            commentService.appendAppointmentTimeslots(comment, commentDTO);
        }

        if (commentDTO.getAppointmentPreferences() != null) {
            commentService.appendAppointmentPreferences(comment, commentDTO);
        }

        if (actionId.equals(APPLICATION_PROVIDE_PARTNER_APPROVAL)) {
            Boolean onCourse = commentDTO.getOnCourse();
            if (onCourse != null) {
                comment.setOnCourse(onCourse);
            }
        }

        return actionService.executeUserAction(application, action, comment);
    }

}
