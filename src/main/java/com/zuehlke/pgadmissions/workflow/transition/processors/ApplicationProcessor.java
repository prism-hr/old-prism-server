package com.zuehlke.pgadmissions.workflow.transition.processors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_REJECTION_SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
public class ApplicationProcessor implements ResourceProcessor<Application> {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private RoleService roleService;

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public void process(Application resource, Comment comment) {
        if (comment.isApplicationAutomatedRejectionComment()) {
            setRejectionReasonSystem(resource, comment);
        }

        if (comment.isApplicationAssignRefereesComment()) {
            appendApplicationReferees(resource, comment);
        }

        if (comment.isApplicationUpdateRefereesComment()) {
            appendApplicationReferees(resource, comment);
        }
    }

    private void setRejectionReasonSystem(Application application, Comment comment) {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(application);
        comment.setRejectionReasonSystem(propertyLoader.loadLazy(APPLICATION_COMMENT_REJECTION_SYSTEM));
    }

    private void appendApplicationReferees(Application application, Comment comment) {
        Role role = roleService.getById(APPLICATION_REFEREE);
        for (User user : applicationService.getApplicationRefereesNotResponded(application)) {
            comment.addAssignedUser(user, role, CREATE);
        }
    }

}
