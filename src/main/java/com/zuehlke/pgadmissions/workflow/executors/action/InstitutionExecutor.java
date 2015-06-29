package com.zuehlke.pgadmissions.workflow.executors.action;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.INSTITUTION_COMMENT_UPDATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
public class InstitutionExecutor implements ActionExecutor {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public ActionOutcomeDTO execute(Integer resourceId, CommentDTO commentDTO) throws Exception {
        User user = userService.getById(commentDTO.getUser());
        Institution institution = institutionService.getById(resourceId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        InstitutionDTO institutionDTO = commentDTO.getResource().getInstitution();
        Comment comment = prepareProcessResourceComment(institution, user, action, institutionDTO, commentDTO);
        institutionService.update(institution, institutionDTO);
        return actionService.executeUserAction(institution, action, comment);
    }

    public Comment prepareProcessResourceComment(Institution institution, User user, Action action, InstitutionDTO institutionDTO, CommentDTO commentDTO)
            throws Exception {
        String commentContent = action.getId().equals(INSTITUTION_VIEW_EDIT) ? applicationContext.getBean(PropertyLoader.class).localize(institution)
                .load(INSTITUTION_COMMENT_UPDATED) : commentDTO.getContent();

        State transitionState = stateService.getById(commentDTO.getTransitionState());
        Comment comment = new Comment().withUser(user).withResource(institution).withContent(commentContent).withAction(action)
                .withTransitionState(transitionState).withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);
        commentService.appendCommentProperties(comment, commentDTO);

        return comment;
    }

}
