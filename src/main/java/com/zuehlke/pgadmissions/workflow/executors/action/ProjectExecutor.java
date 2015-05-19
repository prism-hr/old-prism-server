package com.zuehlke.pgadmissions.workflow.executors.action;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionPartnerDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.services.*;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROJECT_COMMENT_UPDATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;

@Component
public class ProjectExecutor implements ActionExecutor {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private ProjectService projectService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public ActionOutcomeDTO execute(Integer resourceId, CommentDTO commentDTO) throws Exception {
        User user = userService.getById(commentDTO.getUser());
        Project project = projectService.getById(resourceId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        boolean viewEditAction = actionId == PROJECT_VIEW_EDIT;
        String commentContent = viewEditAction ? applicationContext.getBean(PropertyLoader.class).localize(project)
                .load(PROJECT_COMMENT_UPDATED) : commentDTO.getContent();

        OpportunityDTO projectDTO = commentDTO.getResource().getProject();
        LocalDate endDate = projectDTO.getEndDate();

        State transitionState = stateService.getById(commentDTO.getTransitionState());
        if (viewEditAction && !project.getImported() && transitionState == null && (endDate == null || endDate.isAfter(new LocalDate()))) {
            transitionState = stateService.getById(PROJECT_APPROVED);
        }

        InstitutionPartnerDTO partnerDTO = projectDTO.getPartner();
        Comment comment = new Comment().withUser(user).withResource(project).withContent(commentContent).withAction(action)
                .withRemovedPartner(partnerDTO != null && partnerDTO.isEmpty()).withTransitionState(transitionState).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(false);
        commentService.appendCommentProperties(comment, commentDTO);

        projectService.update(resourceId, projectDTO, comment);
        return actionService.executeUserAction(project, action, comment);
    }

}
