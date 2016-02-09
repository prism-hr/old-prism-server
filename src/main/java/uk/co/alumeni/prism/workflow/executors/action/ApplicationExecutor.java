package uk.co.alumeni.prism.workflow.executors.action;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.exceptions.PrismValidationException;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
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
        PrismAction actionId = commentDTO.getAction();
        User user = userService.getById(commentDTO.getUser());
        Application application = entityService.getById(Application.class, commentDTO.getResource().getId());

        if (actionId.equals(APPLICATION_COMPLETE)) {
            BeanPropertyBindingResult errors = applicationService.validateApplication(application);
            if (errors.hasErrors()) {
                throw new PrismValidationException("Application not completed", errors);
            }

            application.setShared(BooleanUtils.isTrue(commentDTO.getShared()));
            application.setOnCourse(BooleanUtils.isTrue(commentDTO.getOnCourse()));
        }

        Action action = actionService.getById(actionId);
        Comment comment = commentService.prepareProcessResourceComment(application, user, action, commentDTO);

        return actionService.executeUserAction(application, action, comment);
    }

}
