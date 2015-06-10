package com.zuehlke.pgadmissions.workflow.executors.action;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationPositionDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentApplicationOfferDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentApplicationPositionDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
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
    public ActionOutcomeDTO execute(Integer resourceId, CommentDTO commentDTO) throws Exception {
        Application application = entityService.getById(Application.class, resourceId);
        PrismAction actionId = commentDTO.getAction();

        User user = userService.getById(commentDTO.getUser());
        if (actionId == APPLICATION_COMPLETE) {
            BeanPropertyBindingResult errors = applicationService.validateApplication(application);
            if (errors.hasErrors()) {
                throw new PrismValidationException("Application not completed", errors);
            }

            application.setRetain(commentDTO.getApplicationRetain());
            user.getUserAccount().setSendApplicationRecommendationNotification(commentDTO.getApplicationRecommend());
        }

        Action action = actionService.getById(actionId);
        User delegateUser = userService.getById(commentDTO.getDelegateUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());

        Comment comment = new Comment().withUser(user).withResource(application).withContent(commentDTO.getContent()).withDelegateUser(delegateUser)
                .withAction(action).withTransitionState(transitionState).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(BooleanUtils.isTrue(commentDTO.getDeclinedResponse())).withApplicationEligible(commentDTO.getApplicationEligible())
                .withApplicationInterested(commentDTO.getApplicationInterested()).withApplicationRating(commentDTO.getApplicationRating())
                .withRecruiterAcceptAppointment(commentDTO.getRecruiterAcceptAppointment())
                .withApplicationReserveStatus(commentDTO.getApplicationReserveStatus());

        CommentApplicationPositionDetailDTO positionDetailDTO = commentDTO.getPositionDetail();
        if (positionDetailDTO != null) {
            comment.setPositionDetail(new CommentApplicationPositionDetail().withPositionTitle(positionDetailDTO.getPositionTitle()).withPositionDescription(
                    positionDetailDTO.getPositionDescription()));
        }

        CommentApplicationOfferDetailDTO offerDetailDTO = commentDTO.getOfferDetail();
        if (offerDetailDTO != null) {
            comment.setOfferDetail(new CommentApplicationOfferDetail().withPositionProvisionStartDate(offerDetailDTO.getPositionProvisionalStartDate())
                    .withAppointmentConditions(offerDetailDTO.getAppointmentConditions()));
        }

        commentService.appendCommentProperties(comment, commentDTO);

        if (actionId == PrismAction.APPLICATION_COMPLETE) {
            Role refereeRole = entityService.getById(Role.class, PrismRole.APPLICATION_REFEREE);
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

        if (commentDTO.getRejectionReason() != null) {
            commentService.appendRejectionReason(comment, commentDTO);
        }

        return actionService.executeUserAction(application, action, comment);
    }

}
