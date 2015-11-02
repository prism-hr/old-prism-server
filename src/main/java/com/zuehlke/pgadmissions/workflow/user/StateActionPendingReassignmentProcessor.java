package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.StateActionPending;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class StateActionPendingReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (StateActionPending oldStateActionPending : oldUser.getStateActionPendings()) {
            userService.mergeUserAssignmentStrict(oldStateActionPending, newUser, userProperty);
        }
    }

}
