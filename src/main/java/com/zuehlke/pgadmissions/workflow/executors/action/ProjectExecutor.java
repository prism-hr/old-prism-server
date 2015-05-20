package com.zuehlke.pgadmissions.workflow.executors.action;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROJECT_COMMENT_UPDATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.SPONSOR_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SPONSOR;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionPartnerDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ProjectService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
public class ProjectExecutor implements ActionExecutor {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private ProjectService projectService;

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

        if (action.getActionCategory().equals(SPONSOR_RESOURCE)) {
            Comment comment = commentService.prepareResourceParentComment(project, user, action, commentDTO, PROJECT_SPONSOR);
            return actionService.executeUserAction(project, action, comment);
        } else {
            OpportunityDTO programDTO = commentDTO.getResource().getProject();
            Comment comment = prepareProcessResourceComment(project, user, action, programDTO, commentDTO);
            projectService.update(resourceId, programDTO, comment);
            return actionService.executeUserAction(project, action, comment);
        }
    }

    public Comment prepareProcessResourceComment(Project project, User user, Action action, OpportunityDTO projectDTO, CommentDTO commentDTO) throws Exception {
        String commentContent = action.getId().equals(PROJECT_VIEW_EDIT) ? applicationContext.getBean(PropertyLoader.class).localize(project)
                .load(PROJECT_COMMENT_UPDATED) : commentDTO.getContent();

        InstitutionPartnerDTO partnerDTO = projectDTO.getPartner();
        Comment comment = new Comment().withUser(user).withResource(project).withContent(commentContent).withAction(action)
                .withRemovedPartner(partnerDTO != null && partnerDTO.isEmpty()).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(false);
        commentService.appendCommentProperties(comment, commentDTO);

        return comment;
    }

}
