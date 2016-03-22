package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.StateActionPending;
import uk.co.alumeni.prism.services.UserService;

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
