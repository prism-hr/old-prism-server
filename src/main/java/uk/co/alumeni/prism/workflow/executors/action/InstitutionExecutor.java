package uk.co.alumeni.prism.workflow.executors.action;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.resource.InstitutionDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.InstitutionService;
import uk.co.alumeni.prism.services.UserService;

@Component
public class InstitutionExecutor implements ActionExecutor {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private UserService userService;

    @Override
    public ActionOutcomeDTO execute(CommentDTO commentDTO) {
        PrismAction actionId = commentDTO.getAction();
        User user = userService.getById(commentDTO.getUser());
        Institution institution = institutionService.getById(commentDTO.getResource().getId());

        ResourceCreationDTO institutionDTO = commentDTO.getResource();

        if (institutionDTO.getClass().equals(InstitutionDTO.class)) {
            institutionService.update(institution, (InstitutionDTO) institutionDTO);
        }

        Action action = actionService.getById(actionId);
        Comment comment = commentService.prepareProcessResourceComment(institution, user, action, commentDTO);

        return actionService.executeUserAction(institution, action, comment);
    }

}

