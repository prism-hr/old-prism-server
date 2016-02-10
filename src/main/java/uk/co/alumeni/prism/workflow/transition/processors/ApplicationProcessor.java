package uk.co.alumeni.prism.workflow.transition.processors;

import static uk.co.alumeni.prism.domain.definitions.PrismRejectionReason.POSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.RoleService;

@Component
public class ApplicationProcessor implements ResourceProcessor<Application> {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private RoleService roleService;

    @Override
    public void process(Application resource, Comment comment) {
        if (comment.isApplicationAutomatedRejectionComment()) {
            comment.setRejectionReason(POSITION);
        }

        if (comment.isApplicationAssignRefereesComment()) {
            appendApplicationReferees(resource, comment);
        }

        if (comment.isApplicationUpdateRefereesComment()) {
            appendApplicationReferees(resource, comment);
        }
    }

    private void appendApplicationReferees(Application application, Comment comment) {
        Role role = roleService.getById(APPLICATION_REFEREE);
        for (User user : applicationService.getApplicationRefereesNotResponded(application)) {
            comment.addAssignedUser(user, role, CREATE);
        }
    }

}
