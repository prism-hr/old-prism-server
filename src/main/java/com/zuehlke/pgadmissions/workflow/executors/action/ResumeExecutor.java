package com.zuehlke.pgadmissions.workflow.executors.action;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resume;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.services.*;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

import javax.inject.Inject;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.RESUME_COMPLETE;

@Component
public class ResumeExecutor implements ActionExecutor {

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
        Resume resume = entityService.getById(Resume.class, commentDTO.getResource().getId());
        PrismAction actionId = commentDTO.getAction();

        User user = userService.getById(commentDTO.getUser());
        if (actionId.equals(RESUME_COMPLETE)) {
            BeanPropertyBindingResult errors = applicationService.validateApplication(resume);
            if (errors.hasErrors()) {
                throw new PrismValidationException("Resume not completed", errors);
            }

            resume.setRetain(commentDTO.getApplicationRetain());
            user.getUserAccount().setSendApplicationRecommendationNotification(commentDTO.getApplicationRecommend());
        }

        Action action = actionService.getById(actionId);
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());

        Comment comment = new Comment().withUser(user).withResource(resume).withAction(action).withContent(commentDTO.getContent()).withRating(commentDTO.getRating())
                .withDeclinedResponse(BooleanUtils.isTrue(commentDTO.getDeclinedResponse())).withTransitionState(transitionState).withCreatedTimestamp(new DateTime());

        commentService.appendCommentProperties(comment, commentDTO);
        return actionService.executeUserAction(resume, action, comment);
    }

}
