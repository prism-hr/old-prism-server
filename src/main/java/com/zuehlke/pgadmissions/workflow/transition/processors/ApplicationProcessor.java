package com.zuehlke.pgadmissions.workflow.transition.processors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_REJECTION_SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
public class ApplicationProcessor implements ResourceProcessor {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private RoleService roleService;

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Application application = (Application) resource;
        if (comment.isApplicationAutomatedRejectionComment()) {
            setRejectionReasonSystem(resource, comment);
        }

        if (comment.isApplicationAssignRefereesComment()) {
            appendApplicationReferees(application, comment);
        }

        if (comment.isApplicationUpdateRefereesComment()) {
            appendApplicationReferees(application, comment);
        }
    }

    private void setRejectionReasonSystem(Resource resource, Comment comment) {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(resource);
        comment.setRejectionReasonSystem(propertyLoader.load(APPLICATION_COMMENT_REJECTION_SYSTEM));
    }

    private void appendApplicationReferees(Application application, Comment comment) {
        Role refereeRole = roleService.getById(APPLICATION_REFEREE);
        for (User referee : applicationService.getApplicationRefereesNotResponded(application)) {
            comment.addAssignedUser(referee, refereeRole, CREATE);
        }
    }

}
