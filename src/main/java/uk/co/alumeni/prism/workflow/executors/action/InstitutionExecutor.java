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
        User user = userService.getById(commentDTO.getUser());
        Institution institution = institutionService.getById(commentDTO.getResource().getId());

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        InstitutionDTO institutionDTO = (InstitutionDTO) commentDTO.getResource();
        Comment comment = commentService.prepareProcessResourceComment(institution, user, action, commentDTO);
        institutionService.update(institution, institutionDTO);

        return actionService.executeUserAction(institution, action, comment);
    }

}

