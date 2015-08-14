package com.zuehlke.pgadmissions.workflow.user;

import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ApplicationSupervisorReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (ApplicationSupervisor oldApplicationSupervisor : oldUser.getApplicationSupervisors()) {
            userService.mergeUserAssignmentStrict(oldApplicationSupervisor, newUser, userProperty);
        }
    }

}
