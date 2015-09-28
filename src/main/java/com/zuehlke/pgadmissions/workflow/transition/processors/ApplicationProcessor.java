package com.zuehlke.pgadmissions.workflow.transition.processors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismRejectionReason.POSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.RoleService;

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
