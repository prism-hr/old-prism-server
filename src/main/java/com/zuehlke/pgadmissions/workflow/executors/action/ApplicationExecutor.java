package com.zuehlke.pgadmissions.workflow.executors.action;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentPositionDetail;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentOfferDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentPositionDetailDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.UserService;

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

            application.setShared(commentDTO.getApplicationShared());
            user.getUserAccount().setSendApplicationRecommendationNotification(commentDTO.getApplicationRecommend());
        }

        Action action = actionService.getById(actionId);
        User delegateUser = userService.getById(commentDTO.getDelegateUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());

        Comment comment = new Comment().withUser(user).withResource(application).withContent(commentDTO.getContent()).withDelegateUser(delegateUser)
                .withAction(action).withTransitionState(transitionState).withRating(commentDTO.getRating()).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(BooleanUtils.isTrue(commentDTO.getDeclinedResponse())).withApplicationIdentified(commentDTO.getApplicationIdentified())
                .withApplicationEligible(commentDTO.getApplicationEligible()).withApplicationInterested(commentDTO.getApplicationInterested())
                .withRecruiterAcceptAppointment(commentDTO.getRecruiterAcceptAppointment()).withApplicantAcceptAppointment(commentDTO.getApplicantAcceptAppointment())
                .withRejectionReason(commentDTO.getRejectionReason());

        CommentPositionDetailDTO positionDetailDTO = commentDTO.getPositionDetail();
        if (positionDetailDTO != null) {
            comment.setPositionDetail(new CommentPositionDetail().withPositionTitle(positionDetailDTO.getPositionTitle()).withPositionDescription(
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

        return actionService.executeUserAction(application, action, comment);
    }

}
